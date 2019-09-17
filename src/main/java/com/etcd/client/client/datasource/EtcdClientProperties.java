package com.etcd.client.client.datasource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:etcd-server.properties")
public class EtcdClientProperties
{
    @Value("${etcd_server_host:'127.0.0.1'}")
    private String etcdServerHost;
    /**
     * Retrieve expected from application.properties files. Then, if not find
     * use default value 2379
     */
    @Value("${etcd_server_port:2379}")
    private int etcdServerPort;

    @Value("${etcd_server_maxNumberOfTries:10}")
    private int maxNumberOfTriesEtcd;

    @Value("${etcd_server_delayBetweenTries:2}")
    private int delayBetweenTriesEtcd;

    public String getEtcdServerHost()
    {
	return etcdServerHost;
    }

    public void setEtcdServerHost(String etcdServerHost)
    {
	this.etcdServerHost = etcdServerHost;
    }

    public int getEtcdServerPort()
    {
	return etcdServerPort;
    }

    public void setEtcdServerPort(int etcdServerPort)
    {
	this.etcdServerPort = etcdServerPort;
    }

    public int getMaxNumberOfTriesEtcd()
    {
	return maxNumberOfTriesEtcd;
    }

    public void setMaxNumberOfTriesEtcd(int maxNumberOfTriesEtcd)
    {
	this.maxNumberOfTriesEtcd = maxNumberOfTriesEtcd;
    }

    public int getDelayBetweenTriesEtcd()
    {
	return delayBetweenTriesEtcd;
    }

    public void setDelayBetweenTriesEtcd(int delayBetweenTriesEtcd)
    {
	this.delayBetweenTriesEtcd = delayBetweenTriesEtcd;
    }

}
