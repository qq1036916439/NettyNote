package com.zzq.demo01EchoService.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;

import java.net.InetSocketAddress;

public class EchoService {
    private final int port;

    public EchoService(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        try {
            new EchoService(8080).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() throws Exception {
        final EchoSrverHandler echoSrverHandler = new EchoSrverHandler();
       //指定新来接的处理器
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //创建一个Bootstrap的实例对象
            ServerBootstrap b = new ServerBootstrap();

            b.group(group)
                    .channel(NioSctpServerChannel.class)
                    //指定监听服务的端口
                    .localAddress(new InetSocketAddress(port))
                    //当一个新连接被处理时。channel会被自动创建，ChannelInitializer将实例添加到Channel的ChannelPipeline中去
                    .childHandler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(echoSrverHandler);
                        }
                    });
            //绑定服务器
            ChannelFuture f = b.bind().sync();
            //自动阻塞程序知道服务器关闭
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }

    }
}
