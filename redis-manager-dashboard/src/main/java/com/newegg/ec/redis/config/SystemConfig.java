package com.newegg.ec.redis.config;

import com.google.common.base.Strings;
import com.newegg.ec.redis.exception.ConfigurationException;
import com.newegg.ec.redis.util.SignUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.MultipartConfigElement;
import java.io.File;

import static com.newegg.ec.redis.util.RedisUtil.CLUSTER;
import static com.newegg.ec.redis.util.RedisUtil.STANDALONE;
import static com.newegg.ec.redis.util.TimeUtil.FIVE_MINUTES;

/**
 * @author Jay.H.Zou
 * @date 7/6/2019
 */
@Configuration
public class SystemConfig implements WebMvcConfigurer {

    public static final String CONFIG_ORIGINAL_PATH = "/data/conf/";

    public static final String MACHINE_PACKAGE_ORIGINAL_PATH = "/data/machine/";

    public static final String AVATAR_PATH = "/data/avatar/";

    @Value("${server.port}")
    private int serverPort;

    @Value("${redis-manager.install.conf-path}")
    private String configPath;

    @Value("${redis-manager.install.machine.package-path}")
    private String machinePackagePath;

    @Value("${redis-manager.auth.avatar-path}")
    private String avatarPath;

    @Value("${redis-manager.install.humpback.enabled:false}")
    private boolean humpbackEnabled;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (Strings.isNullOrEmpty(configPath)) {
            throw new ConfigurationException("conf-path is empty.");
        }
        File file = new File(configPath);
        if (!file.exists()) {
            File clusterConfPath = new File(configPath + CLUSTER);
            clusterConfPath.mkdirs();
            File standaloneConfPath = new File(configPath + STANDALONE);
            standaloneConfPath.mkdirs();
        }

        if (Strings.isNullOrEmpty(machinePackagePath)) {
            throw new ConfigurationException("machine.package-path is empty.");
        }
        File file2 = new File(machinePackagePath);
        if (!file2.exists()) {
            file2.mkdirs();
        }

        if (Strings.isNullOrEmpty(avatarPath)) {
            throw new ConfigurationException("avatar-path is empty.");
        }
        File file3 = new File(avatarPath);
        if (!file3.exists()) {
            file3.mkdirs();
        }
        avatarPath += avatarPath.endsWith(SignUtil.SLASH) ? "" : SignUtil.SLASH;
        registry.addResourceHandler(CONFIG_ORIGINAL_PATH + "**").addResourceLocations("file:" + configPath);
        registry.addResourceHandler(MACHINE_PACKAGE_ORIGINAL_PATH + "**").addResourceLocations("file:" + machinePackagePath);
        registry.addResourceHandler(AVATAR_PATH + "**").addResourceLocations("file:" + avatarPath);
        registry.addResourceHandler(AVATAR_PATH + "**").addResourceLocations("file:" + avatarPath);
    }

    @Override
    public void configureAsyncSupport(final AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(FIVE_MINUTES);
        configurer.registerCallableInterceptors(timeoutInterceptor());
    }

    @Bean
    public TimeoutCallableProcessingInterceptor timeoutInterceptor() {
        return new TimeoutCallableProcessingInterceptor();
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //文件最大KB,MB
        factory.setMaxFileSize(DataSize.ofBytes(10485760));
        //设置总上传数据总大小
        factory.setMaxRequestSize(DataSize.ofBytes(10485760));
        return factory.createMultipartConfig();
    }

    public int getServerPort() {
        return serverPort;
    }

    public boolean getHumpbackEnabled() {
        return humpbackEnabled;
    }

    public String getAvatarPath() {
        return avatarPath;
    }
}
