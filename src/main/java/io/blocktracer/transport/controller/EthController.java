package io.blocktracer.transport.controller;

import io.blocktracer.transport.service.ParityRpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
public class EthController {


    @Autowired
    ParityRpcService parityRpcService;

    @PostMapping("/elastic/make/transaction")
    public String convertTransaction(@RequestBody Integer blockNumber ) throws IOException {

           return parityRpcService.makeElkData(blockNumber,"",1);
    }
}
