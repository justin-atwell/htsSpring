package hedera.starter.models;

import java.io.Serializable;

public class Account implements Serializable {
    private String acountId;

    private String privateKey;
    private String publicKey;
    private String solidityAddress;
}
