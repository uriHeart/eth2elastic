package io.blocktracer.transport.service;

import io.blocktracer.transport.dto.EthTxInsDto;

import java.io.IOException;
import java.util.List;

public interface ElasticSearchService {


    /**
     *  엘라스틱 get 호출
     * @param uri
     * @param t
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> T elasticHttpGet(String uri, Class<T> t) throws IOException;

    /**
     * 엘라스틱 post 호출
     * @param uri
     * @param param
     * @return
     * @throws IOException
     */
    String  elasticHttpPost(String uri, Object param) throws IOException;

    /**
     * 엘라스틱 uri 생성
     * @param dest
     * @param query
     * @return
     */
    String makeElasticUri(String dest, String query);

    /**
     * 엘라스틱 서치에 저장된 Max Block Number
     * @return
     * @throws IOException
     */
    String getMaxEthBlockNumber() throws IOException;

    /**
     * 엘라스틱 서치에 저장된 Min Block Number
     * @return
     * @throws IOException
     */
    String getMinEthBlockNumber() throws IOException;


    /**
     * 엘라스틱 서치 인서트 벌크처리(블록단위 입력)
     * @param insDtoList
     * @return
     * @throws IOException
     */
    String addEthTxBulk(List<EthTxInsDto> insDtoList) throws IOException;

}
