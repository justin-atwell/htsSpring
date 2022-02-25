package hedera.starter.controller;

import com.hedera.hashgraph.sdk.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

@RestController
@RequestMapping(path = "/account")
public class AccountController {

    PrivateKey OPERATOR_KEY = PrivateKey.fromString(Objects.requireNonNull(Dotenv.load().get("OPERATOR_KEY")));

    @GetMapping("")
    public AccountId getAccountInfo() throws PrecheckStatusException, TimeoutException, ReceiptStatusException {
        AccountId OPERATOR_ID = AccountId.fromString(Objects.requireNonNull(Dotenv.load().get("OPERATOR_ID")));

        Client client = Client.forTestnet();
        client.setOperator(OPERATOR_ID, OPERATOR_KEY);

        TopicCreateTransaction topicCreateTransaction = new TopicCreateTransaction();

        TransactionResponse response = topicCreateTransaction.execute(client);
        TransactionReceipt receipt = response.getReceipt(client);

        TopicId topicId = receipt.topicId;

        TopicMessageSubmitTransaction submitMessage = new TopicMessageSubmitTransaction()
                .setTopicId(topicId)
                .setMessage("Chicken bacon ranch");

        new TopicMessageQuery()
                .setTopicId(topicId)
                .subscribe(client, resp -> {
                    String messageAsString = new String(resp.contents, StandardCharsets.UTF_8);
                    System.out.println(resp.consensusTimestamp + " received topic message: " + messageAsString);
                });

        return OPERATOR_ID;
    }

    private Function<byte[],byte[]> generateSignatureFunction() {
        // Retrieve the Private Key from the .env file

        // return a function that will sign the byte array transaction during the signWith method
        return (
                t -> OPERATOR_KEY.sign(t)
        );
    }
}
