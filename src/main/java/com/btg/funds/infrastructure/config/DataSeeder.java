package com.btg.funds.infrastructure.config;

import com.btg.funds.infrastructure.persistence.repository.SpringClientRepository;
import com.btg.funds.infrastructure.persistence.repository.SpringFundRepository;
import com.btg.funds.infrastructure.persistence.document.ClientDocument;
import com.btg.funds.infrastructure.persistence.document.FundDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final SpringFundRepository fundRepository;
    private final SpringClientRepository clientRepository;

    @Override
    public void run(String... args) {
        seedFunds();
        seedClient();
    }

    private void seedFunds() {
        if (fundRepository.count() > 0) return;

        List<FundDocument> funds = List.of(
                fund("1", "FPV_BTG_PACTUAL_RECAUDADORA", 75000, "FPV"),
                fund("2", "FPV_BTG_PACTUAL_ECOPETROL", 125000, "FPV"),
                fund("3", "DEUDAPRIVADA", 50000, "FIC"),
                fund("4", "FDO-ACCIONES", 250000, "FIC"),
                fund("5", "FPV_BTG_PACTUAL_DINAMICA", 100000, "FPV")
        );
        fundRepository.saveAll(funds);
        log.info("[SEEDER] {} fondos cargados", funds.size());
    }

    private void seedClient() {
        if (clientRepository.existsById("1")) return;

        ClientDocument client = ClientDocument.builder()
                .id("1")
                .balance(500000)
                .notificationPreference("email")
                .contactInfo("cliente@btgpactual.com")
                .activeFundIds(new ArrayList<>())
                .build();
        clientRepository.save(client);
        log.info("[SEEDER] Cliente inicial creado con saldo COP $500.000");
    }

    private FundDocument fund(String id, String name, long minAmount, String category) {
        return FundDocument.builder()
                .id(id)
                .name(name)
                .minAmount(minAmount)
                .category(category)
                .build();
    }
}
