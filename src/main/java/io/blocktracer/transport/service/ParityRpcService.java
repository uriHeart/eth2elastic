package io.blocktracer.transport.service;


import io.blocktracer.transport.dto.EthBlockNumberDto;
import io.blocktracer.transport.dto.EthTxInsDto;
import io.blocktracer.transport.dto.RpcReqDto;

import java.io.IOException;
import java.util.List;

public interface ParityRpcService {

    /**
     * 로컬 Parity 노드에 rcp 명령호출.
     * @param params
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> T callParityRpc(RpcReqDto params, Class<T> classOfT) throws IOException;


    /**
     * 현재 노드의 마지막 블럭넘버를 조회
     *
     * @return
     * @throws IOException
     */
    EthBlockNumberDto getEtherBlockNumber() throws IOException;


    /**
     * 이더리움 블록의 트랜잭션 정보를 가져온다.
     * @param hexBlockNumber
     * @return
     * @throws IOException
     */
    List<EthTxInsDto> getEthBlockDataByNumber(String hexBlockNumber) throws IOException;

    /**
     * 매1초 이더리움 노드를 검색하여 신규생성된 데이터를 엘라스틱 서치에 생성한다.
     * @return
     */
    String makeEthTxDataToElk() throws IOException;


    /**
     * 과거의 이더리움 노드의 데이터를 엘라스틱 서치에 생성한다.(초기이관용)
     * @return
     */
    String makeBeforeEthTxDataToElk() throws IOException;


    /**
     * 노드데이터 이관시점 이후 데이터를 엘라스틱 서치에 생성한다.(초기이관용)
     * @return
     */
    String makeMaxEthTxDataToElk() throws IOException;



    /**
     * elk 에 트랜잭션 데이터를 생성한다.
     * @param blockNumber
     * @param min_max
     * @return
     * @throws IOException
     */
    String makeElkData(int blockNumber ,String min_max,int bulkSize) throws IOException;


    /**
     * 처리한 block을 기록한다
     * 성공시 tartget:"check/block" , 실패시 target:"error/block"
     * @param target
     * @param blockNumber
     * @return
     */
    String addProcessedBlockNumber(String target, String blockNumber) throws IOException;


}
