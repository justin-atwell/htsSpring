package hedera.starter.controller;

import com.hedera.hashgraph.sdk.AccountCreateTransaction;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.Transaction;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionResponse;
import com.hedera.hashgraph.sdk.TransferTransaction;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Objects;

public final class AccountController {

    private static final AccountId OPERATOR_ID = AccountId.fromString(Objects.requireNonNull(Dotenv.load().get("OPERATOR_ID")));
    private static final PrivateKey OPERATOR_KEY = PrivateKey.fromString(Objects.requireNonNull(Dotenv.load().get("OPERATOR_KEY")));

    private AccountController() {
    }

    public static void main(String[] args) throws Exception {

        Client client = Client.forTestnet();
        client.setOperator(OPERATOR_ID, OPERATOR_KEY);

        PrivateKey userKey = PrivateKey.generate();

        AccountId userId = new AccountCreateTransaction()
                .setKey(userKey.getPublicKey())
                .setInitialBalance(new Hbar(5))
                .execute(client).getReceipt(client).accountId;

        System.out.println("Submitting transaction");
//        TransactionId transactionId = TransactionId.generate(Justin);

        TransferTransaction transaction = new TransferTransaction()
//            .setTransactionId(transactionId)
                .addHbarTransfer(userId, new Hbar(-1))
                .addHbarTransfer(OPERATOR_ID, new Hbar(1))
                .freezeWith(client)
                .sign(OPERATOR_KEY);

        byte[] transBytes = transaction.toBytes();
        // 3rd act send bytes to client
        // client could either sign the bytes and return signature to 3rd act who submit to hedera
        // client signs and submits to hedera

        Transaction<?> transaction1 = Transaction.fromBytes(transBytes);

        transaction1.sign(userKey);

        Client client2 = Client.forTestnet();
        TransactionResponse response = (TransactionResponse) transaction1.execute(client2);
        System.out.println("Getting receipt");

        // assuming I'm the end user here
        TransactionReceipt receipt = response.getReceipt(client2);
//        System.out.println("Transaction id " + transactionId.toString());
    }
}