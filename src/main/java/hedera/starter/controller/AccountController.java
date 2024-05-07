package hedera.starter.controller;

import com.hedera.hashgraph.sdk.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeoutException;

@RestController
public final class AccountController {

    private static final AccountId OPERATOR_ID = AccountId.fromString("");
    private static final PrivateKey OPERATOR_KEY = PrivateKey.fromString("");

    private AccountController() {
    }

    @GetMapping("/accounts")
    public void getEmployee() throws PrecheckStatusException, TimeoutException, ReceiptStatusException {

        Client client = Client.forTestnet();
        client.setOperator(OPERATOR_ID, OPERATOR_KEY);

        TokenCreateTransaction transaction = new TokenCreateTransaction()
                .setTokenName("CBR")
                .setTokenSymbol("cbr")
                .setTreasuryAccountId(OPERATOR_ID)
                .setInitialSupply(50000)
                .setAdminKey(OPERATOR_KEY.getPublicKey())
                .setMaxTransactionFee(new Hbar(30)); //Change the default max transaction fee

//Build the unsigned transaction, sign with admin private key of the token, sign with the token treasury private key, submit the transaction to a Hedera network
        TransactionResponse txResponse = transaction.freezeWith(client).sign(OPERATOR_KEY).execute(client);

//Request the receipt of the transaction
        TransactionReceipt receipt = txResponse.getReceipt(client);

//Get the token ID from the receipt
        TokenId tokenId = receipt.tokenId;

        System.out.println("The new token ID is " + tokenId);

//        TransferTransaction transaction = new TransferTransaction()
//                .addHbarTransfer(userId, new Hbar(-1))
//                .addHbarTransfer(OPERATOR_ID, new Hbar(1))
//                .freezeWith(client)
//                .sign(OPERATOR_KEY);
//
//        byte[] transBytes = transaction.toBytes();
//
//        Transaction<?> transaction1 = Transaction.fromBytes(transBytes);
//
//        transaction1.sign(userKey);
//
//        Client client2 = Client.forTestnet();
//        TransactionResponse response = (TransactionResponse) transaction1.execute(client2);
//        System.out.println("Getting receipt");

        // assuming I'm the end user here
//        TransactionReceipt receipt = response.getReceipt(client2);
//        System.out.println("Transaction id " + transactionId.toString());
    }
}