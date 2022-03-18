package hedera.starter.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class Account implements Serializable {
    private String accountId;

    private String privateKey;
    private String publicKey;
    private String solidityAddress;
}
