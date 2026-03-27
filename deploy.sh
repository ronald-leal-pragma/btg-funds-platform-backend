#!/usr/bin/env bash
# =============================================================================
# deploy.sh — BTG Funds Platform: Full-Stack Deploy a AWS
#
# Uso:
#   ./deploy.sh [opciones]
#
# Opciones:
#   --stack-name   Nombre del stack CloudFormation  (default: btg-funds-prod)
#   --bucket       Bucket S3 para el JAR del Lambda  (default: btg-funds-deploy-<account-id>)
#   --region       Región AWS                         (default: us-east-1)
#   --env          Ambiente (dev|staging|prod)         (default: prod)
#   --sender-email Email verificado en SES             (default: noreply@btgfunds.com)
#   --skip-build   Omitir compilación del backend      (default: false)
#   --skip-frontend Omitir build y sync del frontend   (default: false)
#
# Prerequisitos:
#   1. aws CLI instalado y configurado (aws configure)
#   2. Java 21 + Maven instalados
#   3. Node.js 18+ instalado
#   4. El email --sender-email DEBE estar verificado en SES antes de ejecutar
#      (AWS Console → SES → Verified identities → Verify email address)
#   5. Para SMS con SNS: en sandbox, verificar el número destino en
#      AWS Console → SNS → SMS sandbox → Add phone number
# =============================================================================
set -euo pipefail

# ── Colores ───────────────────────────────────────────────────────────────────
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

log()    { echo -e "${GREEN}[✓]${NC} $*"; }
warn()   { echo -e "${YELLOW}[!]${NC} $*"; }
info()   { echo -e "${BLUE}[→]${NC} $*"; }
error()  { echo -e "${RED}[✗]${NC} $*"; exit 1; }

# ── Defaults ──────────────────────────────────────────────────────────────────
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
STACK_NAME="btg-funds-prod"
REGION="us-east-1"
ENV="prod"
SENDER_EMAIL="noreply@btgfunds.com"
SKIP_BUILD=false
SKIP_FRONTEND=false
DEPLOY_BUCKET="btg-funds-deploy-${ACCOUNT_ID}"

# ── Parseo de argumentos ──────────────────────────────────────────────────────
while [[ $# -gt 0 ]]; do
  case "$1" in
    --stack-name)    STACK_NAME="$2";    shift 2 ;;
    --bucket)        DEPLOY_BUCKET="$2"; shift 2 ;;
    --region)        REGION="$2";        shift 2 ;;
    --env)           ENV="$2";           shift 2 ;;
    --sender-email)  SENDER_EMAIL="$2";  shift 2 ;;
    --skip-build)    SKIP_BUILD=true;    shift ;;
    --skip-frontend) SKIP_FRONTEND=true; shift ;;
    *) error "Argumento desconocido: $1" ;;
  esac
done

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$SCRIPT_DIR"
FRONTEND_DIR="$(dirname "$SCRIPT_DIR")/btg-funds-platform-frontend"
TEMPLATE_PATH="$BACKEND_DIR/cloudformation/template.yml"
JAR_NAME="btg-funds-platform-backend.jar"
JAR_PATH="$BACKEND_DIR/target/btg-funds-platform-backend-0.0.1-SNAPSHOT.jar"

echo ""
echo "╔══════════════════════════════════════════════════════╗"
echo "║     BTG Funds Platform — Deploy a AWS               ║"
echo "╚══════════════════════════════════════════════════════╝"
echo ""
info "Cuenta AWS : $ACCOUNT_ID"
info "Región     : $REGION"
info "Stack      : $STACK_NAME"
info "Bucket     : $DEPLOY_BUCKET"
info "Ambiente   : $ENV"
info "SES Email  : $SENDER_EMAIL"
echo ""

# ── 1. Verificar prerequisitos ────────────────────────────────────────────────
info "Verificando prerequisitos..."

command -v aws  >/dev/null 2>&1 || error "aws CLI no encontrado. Instalar con: brew install awscli"
command -v mvn  >/dev/null 2>&1 || error "Maven no encontrado."
command -v node >/dev/null 2>&1 || error "Node.js no encontrado."
command -v npm  >/dev/null 2>&1 || error "npm no encontrado."

log "Prerequisitos OK"

# ── 2. Bucket S3 para deploy del Lambda ───────────────────────────────────────
info "Asegurando bucket de deploy: s3://$DEPLOY_BUCKET ..."
if ! aws s3 ls "s3://$DEPLOY_BUCKET" --region "$REGION" >/dev/null 2>&1; then
  aws s3 mb "s3://$DEPLOY_BUCKET" --region "$REGION"
  log "Bucket creado: s3://$DEPLOY_BUCKET"
else
  log "Bucket ya existe: s3://$DEPLOY_BUCKET"
fi

# ── 3. Crear tablas DynamoDB si no existen ────────────────────────────────────
info "Verificando tablas DynamoDB..."

create_table_if_missing() {
  local TABLE=$1
  local KEY_SCHEMA=$2
  local ATTR_DEFS=$3

  if aws dynamodb describe-table --table-name "$TABLE" --region "$REGION" >/dev/null 2>&1; then
    log "Tabla ya existe: $TABLE"
  else
    warn "Creando tabla: $TABLE ..."
    aws dynamodb create-table \
      --table-name "$TABLE" \
      --billing-mode PAY_PER_REQUEST \
      --attribute-definitions $ATTR_DEFS \
      --key-schema $KEY_SCHEMA \
      --region "$REGION" \
      --no-cli-pager >/dev/null
    aws dynamodb wait table-exists --table-name "$TABLE" --region "$REGION"
    log "Tabla creada: $TABLE"
  fi
}

create_table_if_missing "Clients" \
  "AttributeName=id,KeyType=HASH" \
  "AttributeName=id,AttributeType=S"

create_table_if_missing "Funds" \
  "AttributeName=id,KeyType=HASH" \
  "AttributeName=id,AttributeType=S"

create_table_if_missing "Transactions" \
  "AttributeName=clientId,KeyType=HASH AttributeName=timestamp,KeyType=RANGE" \
  "AttributeName=clientId,AttributeType=S AttributeName=timestamp,AttributeType=S"

# ── 4. Compilar backend ───────────────────────────────────────────────────────
if [ "$SKIP_BUILD" = false ]; then
  info "Compilando backend Spring Boot..."
  (cd "$BACKEND_DIR" && mvn clean package -DskipTests -q)
  log "Build del backend OK → $JAR_PATH"
else
  warn "Build del backend omitido (--skip-build)"
  [ -f "$JAR_PATH" ] || error "JAR no encontrado: $JAR_PATH. Ejecutar sin --skip-build primero."
fi

# ── 5. Subir JAR a S3 ─────────────────────────────────────────────────────────
info "Subiendo JAR a S3..."
aws s3 cp "$JAR_PATH" "s3://$DEPLOY_BUCKET/$JAR_NAME" --region "$REGION"
log "JAR subido: s3://$DEPLOY_BUCKET/$JAR_NAME"

# ── 6. Deploy CloudFormation ──────────────────────────────────────────────────
info "Desplegando stack CloudFormation: $STACK_NAME ..."
aws cloudformation deploy \
  --stack-name "$STACK_NAME" \
  --template-file "$TEMPLATE_PATH" \
  --capabilities CAPABILITY_NAMED_IAM \
  --region "$REGION" \
  --parameter-overrides \
    Environment="$ENV" \
    LambdaS3Bucket="$DEPLOY_BUCKET" \
    LambdaS3Key="$JAR_NAME" \
    SesVerifiedSender="$SENDER_EMAIL" \
  --no-fail-on-empty-changeset

log "Stack desplegado OK"

# ── 7. Obtener outputs del stack ──────────────────────────────────────────────
info "Obteniendo outputs del stack..."

get_output() {
  aws cloudformation describe-stacks \
    --stack-name "$STACK_NAME" \
    --region "$REGION" \
    --query "Stacks[0].Outputs[?OutputKey=='$1'].OutputValue" \
    --output text
}

API_ENDPOINT=$(get_output "ApiEndpoint")
FRONTEND_BUCKET=$(get_output "FrontendBucketName")
CF_DISTRIBUTION_ID=$(get_output "CloudFrontDistributionId")
CF_DOMAIN=$(get_output "CloudFrontDomain")

log "API Endpoint      : $API_ENDPOINT"
log "Frontend Bucket   : $FRONTEND_BUCKET"
log "CloudFront Domain : $CF_DOMAIN"

# ── 8. Build y deploy del frontend ───────────────────────────────────────────
if [ "$SKIP_FRONTEND" = false ]; then
  [ -d "$FRONTEND_DIR" ] || error "Directorio frontend no encontrado: $FRONTEND_DIR"

  info "Instalando dependencias del frontend..."
  (cd "$FRONTEND_DIR" && npm install --silent)

  info "Compilando frontend React..."
  # VITE_API_BASE se deja vacío → el código usa '/api/v1' por defecto
  # CloudFront enruta /api/v1/* hacia API Gateway automáticamente
  (cd "$FRONTEND_DIR" && npm run build)
  log "Build del frontend OK → $FRONTEND_DIR/dist"

  info "Sincronizando frontend a S3..."
  aws s3 sync "$FRONTEND_DIR/dist/" "s3://$FRONTEND_BUCKET/" \
    --delete \
    --region "$REGION" \
    --cache-control "public, max-age=31536000, immutable" \
    --exclude "index.html"

  # index.html sin caché para que siempre obtenga la versión más reciente
  aws s3 cp "$FRONTEND_DIR/dist/index.html" "s3://$FRONTEND_BUCKET/index.html" \
    --region "$REGION" \
    --cache-control "no-cache, no-store, must-revalidate"

  log "Frontend sincronizado OK"

  info "Invalidando caché de CloudFront..."
  aws cloudfront create-invalidation \
    --distribution-id "$CF_DISTRIBUTION_ID" \
    --paths "/*" \
    --no-cli-pager >/dev/null
  log "Invalidación de CloudFront creada"
else
  warn "Deploy del frontend omitido (--skip-frontend)"
fi

# ── 9. Resumen final ──────────────────────────────────────────────────────────
echo ""
echo "╔══════════════════════════════════════════════════════╗"
echo "║            ✅  Deploy completado                    ║"
echo "╚══════════════════════════════════════════════════════╝"
echo ""
echo "  Frontend (CloudFront) : $CF_DOMAIN"
echo "  Backend (API Gateway) : $API_ENDPOINT"
echo ""
echo "  Credenciales demo:"
echo "    Email   : user@email.com"
echo "    Password: btg1234"
echo ""
warn "NOTA SES (email): El email '$SENDER_EMAIL' debe estar verificado en"
warn "      SES → Verified identities. En sandbox, el destinatario también."
warn "NOTA SNS (SMS): En sandbox de SNS, verificar el número destino en"
warn "      SNS → SMS sandbox → Add phone number."
echo ""
