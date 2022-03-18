package hedera.starter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.hashgraph.sdk.*;
import hedera.starter.models.Account;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping(path = "/account")
public class AccountController {
    AccountId accountId;

    @GetMapping("")
    public Account getAccountInfo() throws PrecheckStatusException, TimeoutException {
        Client client = Client.forTestnet();
        accountId = AccountId.fromString(Objects.requireNonNull(getEnv().get("OPERATOR_ID")));

        client.setOperator(accountId, PrivateKey.fromString(getEnv().get("OPERATOR_KEY")));

        Hbar cost = new AccountInfoQuery().setAccountId(accountId).getCost(client);

        AccountInfo info = new AccountInfoQuery()
                .setAccountId(accountId).execute(client);

        Account account = new Account();
        account.setAccountId(info.accountId.toString());

        return account;
    }

    static Dotenv getEnv() {
        return Dotenv.load();
    }

}