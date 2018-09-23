package io.blocktracer.transport.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EthBlockNumberDto {
    String blockNumber;
    String hexBlockNumber;
}
