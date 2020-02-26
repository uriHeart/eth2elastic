package io.blocktracer.transport.service.impl;

import com.google.gson.Gson;
import io.blocktracer.transport.dto.*;
import io.blocktracer.transport.service.ElasticSearchService;
import io.blocktracer.transport.service.ParityRpcService;
import io.blocktracer.transport.util.EthDateUtil;
import io.blocktracer.transport.util.EthNumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.jni.OS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@EnableScheduling
@Service
public class ParityRpcServiceImpl implements ParityRpcService {

    @Value("${block.tracer.parity.rpc.host}")
    private String rpcHost;

    @Value("${block.tracer.parity.rpc.port}")
    private String rpcPort;

    @Value("${block.tracer.parity.rpc.get.balance}")
    private String balanceMethod;

    @Value("${block.tracer.parity.rpc.block.number}")
    private String lastBlockNumberMethod;


    @Autowired
    ElasticSearchService elkService;

    @Value("${block.tracer.parity.rpc.divide.value}")
    private String divideBase;

    @Value("${elk.transaction.insert.size}")
    private Integer bulkSize;

    @Override
    public <T> T callParityRpc(RpcReqDto params, Class<T> classOfT) throws IOException {
        HttpPost request = new HttpPost("http://"+rpcHost+":"+rpcPort);

        StringEntity rpcParams =new StringEntity(new Gson().toJson(params));

        request.addHeader("Content-type", "application/json");
        request.setEntity(rpcParams);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = httpClient.execute(request);

        String rpcResult = EntityUtils.toString(httpResponse.getEntity()).replace("\n","");

        T result =  new Gson().fromJson(rpcResult,classOfT);
        return result;
    }


    @Override
    public EthBlockNumberDto getEtherBlockNumber() throws IOException {

        EthBlockNumberDto result = new EthBlockNumberDto();

        RpcReqDto blockNumberReqDto = new RpcReqDto();
        blockNumberReqDto.setMethod(this.lastBlockNumberMethod);

        //node 의 현재 블록넘버 조회
        RpcReqDto nodeData_blockNumber = this.callParityRpc(blockNumberReqDto, RpcReqDto.class);

        String nodeBlockNumber = EthNumberUtil.hexToNumber(nodeData_blockNumber.getResult());

        result.setHexBlockNumber(nodeData_blockNumber.getResult());
        result.setBlockNumber(nodeBlockNumber);

        return result;
    }


    @Override
    public List<EthTxInsDto> getEthBlockDataByNumber(String hexBlockNumber) throws IOException {
        RpcReqDto transactionReqDto = new RpcReqDto();

        transactionReqDto.setMethod("eth_getBlockByNumber");
        Object[] params = {hexBlockNumber,true};
        transactionReqDto.setParams(params);

        //Ethereum block 정보 취득
        EthBlockDto ethBlock = this.callParityRpc(transactionReqDto,EthBlockDto.class);

        EthBlockDataDto blockData = ethBlock.getResult();

        blockData.setGasLimit(EthNumberUtil.hexToNumber(blockData.getGasLimit()));
        blockData.setGasUsed(EthNumberUtil.hexToNumber(blockData.getGasUsed()));
        blockData.setNonce(EthNumberUtil.hexToNumber(blockData.getNonce()));
        blockData.setNumber(EthNumberUtil.hexToNumber(blockData.getNumber()));
        blockData.setDifficulty(EthNumberUtil.hexToNumber(blockData.getDifficulty()));

        //도큐먼트 생성 DTO
        List<EthTxInsDto> insDtoList = new ArrayList<EthTxInsDto>();
        List<EthTxDto> txList = ethBlock.getResult().getTransactions();
        txList.forEach(tx ->{
            //Block Data Mapping
            EthTxInsDto insDto = new EthTxInsDto();
            insDto.setAuthor(blockData.getAuthor());
            insDto.setDifficulty(blockData.getDifficulty());
            insDto.setExtraData(blockData.getExtraData());
            insDto.setGasLimit(blockData.getGasLimit());
            insDto.setGasUsed(blockData.getGasUsed());
            insDto.setBlockHash(blockData.getHash());
            insDto.setLogsBloom(blockData.getLogsBloom());
            insDto.setMiner(blockData.getMiner());
            insDto.setMixHash(blockData.getMixHash());
            insDto.setBlockNonce(blockData.getNonce());
            insDto.setBlockNumber(Long.valueOf(blockData.getNumber()));
            insDto.setParentHash(blockData.getParentHash());
            insDto.setReceiptsRoot(blockData.getReceiptsRoot());
            insDto.setSha3Uncles(blockData.getSha3Uncles());
            insDto.setSize(EthNumberUtil.hexToNumber(blockData.getSize()));
            HashMap<String,Object> txDateUTC =  EthDateUtil.hexToDateAll(blockData.getTimestamp(),"UTC");
            HashMap<String,Object> txDateSeoul =  EthDateUtil.hexToDateAll(blockData.getTimestamp(),"Asia/Seoul");

            insDto.setTimestampUTC((String)txDateUTC.get("string"));
            insDto.setTimestampSeoul((String) txDateSeoul.get("string"));

            insDto.setTxDateUTC((String) txDateUTC.get("string"));
            insDto.setTxDateSeoul((String) txDateSeoul.get("string"));
            insDto.setTimeLong((Long) txDateUTC.get("long"));

            insDto.setTotalDifficulty(EthNumberUtil.hexToNumber(blockData.getTotalDifficulty()));
            insDto.setTransactionsRoot(blockData.getTransactionsRoot());


            //TO_DO 날짜 스트링 및 롱생성

            //Tx Data Mapping
            insDto.setChainId(tx.getChainId());
            insDto.setCondition(tx.getCondition());
            insDto.setCreates(tx.getCreates());
            insDto.setFrom(tx.getFrom());
            insDto.setGas(EthNumberUtil.hexToNumber(tx.getGas()));
            insDto.setGasPrice(EthNumberUtil.hexToGasPrice(tx.getGasPrice()));
            insDto.setHash(tx.getHash());
            insDto.setInput(tx.getInput());
            insDto.setNonce(EthNumberUtil.hexToNumber(tx.getNonce()));
            insDto.setPublicKey(tx.getPublicKey());
            insDto.setRaw(tx.getRaw());
            insDto.setStandardV(tx.getStandardV());
            insDto.setTo(tx.getTo());
            insDto.setTransactionIndex(EthNumberUtil.hexToNumber(tx.getTransactionIndex()));
            insDto.setValue(EthNumberUtil.hexToStringNumber(tx.getValue()));
            insDto.setFloatValue(EthNumberUtil.hexToFlotNumber(tx.getValue()));

            //insDto.setV(tx.getV());
            //insDto.setR(tx.getR());
            //insDto.setS(tx.getS());

            insDtoList.add(insDto);
        });


        return insDtoList;
    }

    //운영시 해제
    @Scheduled(cron ="0/1 * * * * ?")
    @Override
    public String makeEthTxDataToElk() throws IOException {

        //node 의 현재 블록넘버 조회
        EthBlockNumberDto ethBlockNumber = this.getEtherBlockNumber();

        String elkBlockNumber = elkService.getMaxEthBlockNumber();

        //elk의 현재 블록과 node의 블록 넘버가 같으면 pass
        if (Integer.parseInt(ethBlockNumber.getBlockNumber()) == Integer.parseInt(elkBlockNumber)) {
            return "pass";
        }

       int procBlockNumber = Integer.parseInt(elkBlockNumber) ;


        //elk 에 저장된 blockNumber 조회
        String query = String.valueOf(procBlockNumber+1);
        String searchUri = elkService.makeElasticUri("check/block", query);

        HashMap<String, Object> checkBlock = elkService.elasticHttpGet(searchUri, HashMap.class);

        String check = checkBlock.get("found").toString();

        //node의 블록이 elk에 있으면 pass
        if (Boolean.valueOf(check)) {
            return "pass";
        }

        String result = this.makeElkData(procBlockNumber,"Max",1);


        return result;
    }

    //@Scheduled(cron ="0/10 * * * * ?")
    @Override
    public String makeBeforeEthTxDataToElk() throws IOException {

        log.warn("proc start!!");
        String elkBlockNumber = elkService.getMinEthBlockNumber();

        int procBlockNumber = Integer.parseInt(elkBlockNumber);

        //elk 에 저장된 blockNumber 조회
        String query = String.valueOf(procBlockNumber-1);
        String searchUri = elkService.makeElasticUri("check/block", query);

        HashMap<String, Object> checkBlock = elkService.elasticHttpGet(searchUri, HashMap.class);

        String check = checkBlock.get("found").toString();

        log.warn("elk check end!!");

        //node의 블록이 elk에 있으면 pass
        if (Boolean.valueOf(check)) {
            return "pass";
        }

        String result = this.makeElkData(procBlockNumber,"Min",bulkSize);

        return result;
    }


    //max블록 bulk 생성
    //@Scheduled(cron ="0/10 * * * * ?")
    @Override
    public String makeMaxEthTxDataToElk() throws IOException {

         String elkBlockNumber = elkService.getMaxEthBlockNumber();

        int procBlockNumber = Integer.parseInt(elkBlockNumber);

        //elk 에 저장된 blockNumber 조회
        String query = String.valueOf(procBlockNumber+1);
        String searchUri = elkService.makeElasticUri("check/block", query);

        HashMap<String, Object> checkBlock = elkService.elasticHttpGet(searchUri, HashMap.class);

        String check = checkBlock.get("found").toString();

        log.warn("elk check end!!");


        //node의 블록이 elk에 있으면 pass
        if (Boolean.valueOf(check)) {
            return "pass";
        }

        String result = this.makeElkData(procBlockNumber,"Max",bulkSize);


        return result;
    }

    @Override
    public String makeElkData(int blockNumber,String min_max,int bulkSize) throws IOException {
        int procBlockNumber = blockNumber;
        String blockHex = null;
        List<EthTxInsDto> insDtoList = new ArrayList<EthTxInsDto>();
        List<String> procBlockList = new ArrayList<String>();
        try {

            for(int i=0; i < bulkSize; i++){

                if("Min".equals(min_max)){
                    procBlockNumber = procBlockNumber - 1;
                }else if("Max".equals(min_max)){
                    procBlockNumber = procBlockNumber +1;
                }

                procBlockList.add(String.valueOf(procBlockNumber));
                //이더리움 블록 데이터 조회
                BigInteger bi = new BigInteger(String.valueOf(procBlockNumber));
                blockHex ="0x" + bi.toString(16);

                insDtoList.addAll(this.getEthBlockDataByNumber(blockHex));
            }

            if(insDtoList.size()==0){

                //blockNumber 저장
                this.addProcessedBlockNumber("check/block",String.valueOf(procBlockNumber));
                return "tx list is 0 !!";
            }

            //ELK 호출하여 데이터 저장
            //데이터 저장시에는 도큐먼트에 트랜잭션과 블록단위의 데이터를 전부 넣는다
            elkService.addEthTxBulk(insDtoList);

            log.warn("Success "+min_max+" Block:"+procBlockNumber+" $$ Block Hex:"+blockHex);

        }catch(Exception e){

            log.error("Error Block:"+procBlockNumber+" $$ Block Hex:"+blockHex);
            log.error("Error Message :"+e.getMessage());
            StackTraceElement[] elem = e.getStackTrace();
            for ( int i = 0; i < elem.length; i++ )
                log.error(elem[i].toString());

            //blockNumber 저장
            elkService.addEthBlockNumberBulk(procBlockList,"error");

            return "false";
        }

        //blockNumber 저장
        String result = elkService.addEthBlockNumberBulk(procBlockList,"check");

        return result;
    }

    @Override
    public String addProcessedBlockNumber(String target, String blockNumber) throws IOException {
        String blockSaveUri = elkService.makeElasticUri(target, blockNumber);
        EthBlockNumberInsDto blockNumberInsDto = new EthBlockNumberInsDto();
        blockNumberInsDto.setInsert(true);


        blockNumberInsDto.setBlockNumber(Integer.parseInt(blockNumber));
        String result = elkService.elasticHttpPost(blockSaveUri, blockNumberInsDto);

        return result;
    }
}
