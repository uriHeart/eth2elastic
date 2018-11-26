package io.blocktracer.transport.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EthTxInsDto {
    String author;
    String difficulty;
    String extraData;
    String gasLimit;
    String gasUsed;
    String blockHash;
    String blockNonce;
    String logsBloom;
    String miner;
    String mixHash;
    String nonce;
    String parentHash;
    String receiptsRoot;
    String sha3Uncles;
    String size;
    String stateRoot;
    String timestampUTC;
    Long   timeLong;
    String timestampSeoul;
    String txDateUTC;
    String txDateSeoul;
    String totalDifficulty;
    String transactionsRoot;
    Long blockNumber;
    String chainId;
    String condition;
    String creates;
    String from;
    String gas;
    String gasPrice;
    String hash;
    String input;
    String publicKey;
    String r;
    String raw;
    String s;
    String standardV;
    String to;
    String transactionIndex;
    String v;
    String value;
    Float floatValue;

}
