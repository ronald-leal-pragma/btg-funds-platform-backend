package com.btg.funds.infrastructure.config;

import com.btg.funds.domain.model.Fund;
import com.btg.funds.domain.model.Client;
import com.btg.funds.domain.repository.FundRepository;
import com.btg.funds.domain.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final FundRepository fundRepository;
    private final ClientRepository clientRepository;

    @Override
    public void run(String... args) {
        try {
            seedClients();
            seedFunds();
        } catch (Exception e) {
            log.error("[SEEDER] Error durante seeding — la app continúa sin datos semilla: {}", e.getMessage(), e);
        }
    }

    private void seedClients() {
        var existing = clientRepository.findById("1");

        if (existing.isPresent()) {
            var c = existing.get();
            if (c.email() == null || c.email().isBlank()) {
                var migrated = new Client(
                        c.id(), c.balance(), c.notificationPreference(),
                        c.contactInfo(), c.activeFundIds(),
                        "user@email.com", "btg1234"
                );
                clientRepository.save(migrated);
                log.info("[SEEDER] Cliente id=1 migrado con email y password");
            }
            return;
        }

        var client = new Client(
                "1",
                500_000L,
                "email",
                "user@email.com",
                java.util.List.of("1", "3"),
                "user@email.com",
                "btg1234"
        );
        clientRepository.save(client);
        log.info("[SEEDER] Cliente inicial creado: id=1");
    }

    private void seedFunds() {
        if (!fundRepository.findAll().isEmpty()) return;

        List<Fund> funds = List.of(
                new Fund("1", "FPV_BTG_PACTUAL_RECAUDADORA", 75_000, "FPV"),
                new Fund("2", "FPV_BTG_PACTUAL_ECOPETROL", 125_000, "FPV"),
                new Fund("3", "DEUDAPRIVADA", 50_000, "FIC"),
                new Fund("4", "FDO-ACCIONES", 250_000, "FIC"),
                new Fund("5", "FPV_BTG_PACTUAL_DINAMICA", 100_000, "FPV")
        );
        funds.forEach(fundRepository::save);
        log.info("[SEEDER] {} fondos cargados", funds.size());
    }
}
