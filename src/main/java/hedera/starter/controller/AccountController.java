package hedera.starter.controller;

import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.HederaStatusException;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.account.AccountInfo;
import com.hedera.hashgraph.sdk.account.AccountInfoQuery;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping(path = "/account")
public class AccountController {
    AccountId accountId;


    @GetMapping("")
    public AccountInfo getAccountInfo() throws HederaStatusException {
        Client client = Client.forTestnet();
        accountId = AccountId.fromString(Objects.requireNonNull(getEnv().get("OPERATOR_ID")));

        client.setOperator(accountId, Ed25519PrivateKey.fromString(""));

        long cost = new AccountInfoQuery().setAccountId(accountId).getCost(client);

        AccountInfo info = new AccountInfoQuery()
                .setAccountId(accountId).setQueryPayment(cost + cost / 50).execute(client);

        return info;
    }

    static Dotenv getEnv() {
        return Dotenv.load();
    }

}
