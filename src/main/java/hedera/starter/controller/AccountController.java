package hedera.starter.controller;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping(path = "/account")
public class AccountController {
    private static final AccountId OPERATOR_ID = AccountId.fromString(Objects.requireNonNull(Dotenv.load().get("OPERATOR_ID")));
    private static final PrivateKey OPERATOR_KEY = PrivateKey.fromString(Objects.requireNonNull(Dotenv.load().get("OPERATOR_KEY")));

    @GetMapping("")
    public AccountId getAccountInfo() throws PrecheckStatusException, TimeoutException, ReceiptStatusException, InvalidProtocolBufferException {
        Client client = Client.forTestnet();
        client.setOperator(OPERATOR_ID, OPERATOR_KEY);

        PrivateKey userKey = PrivateKey.generate();

        AccountId userId = new AccountCreateTransaction()
                .setKey(userKey.getPublicKey())
                .setInitialBalance(new Hbar(5))
                .execute(client).getReceipt(client).accountId;

        System.out.println("Submitting transaction");

        TransferTransaction transaction = new TransferTransaction()
                .addHbarTransfer(userId, new Hbar(-1))
                .addHbarTransfer(OPERATOR_ID, new Hbar(1))
                .freezeWith(client)
                .sign(OPERATOR_KEY);

        byte[] transBytes = transaction.toBytes();
        // The transaction bytes are sent to the client.
        // The client could sign the bytes and return the signature to the application
        // or the client could sign the bytes and submit them to the Hedera network.

        Transaction<?> transaction1 = Transaction.fromBytes(transBytes);
        transaction1.sign(userKey);
        //these lines represent a given user (created above) signing a TransferTransaction

        Client client2 = Client.forTestnet();
        TransactionResponse response = transaction1.execute(client2);
        System.out.println("Getting receipt");

        TransactionReceipt receipt = response.getReceipt(client2);
        System.out.println("Transaction id " + receipt.status);

        return userId;
    }
}
