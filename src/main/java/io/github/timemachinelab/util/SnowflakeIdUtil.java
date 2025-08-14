package io.github.timemachinelab.util;

import org.springframework.stereotype.Component;

/**
 * 雪花算法ID生成器
 * 64位ID结构：1位符号位 + 41位时间戳 + 10位机器ID + 12位序列号
 */
@Component
public class SnowflakeIdUtil {
    
    // 起始时间戳 (2023-01-01 00:00:00)
    private static final long START_TIMESTAMP = 1672531200000L;
    
    // 机器ID位数
    private static final long MACHINE_ID_BITS = 10L;
    
    // 序列号位数
    private static final long SEQUENCE_BITS = 12L;
    
    // 机器ID最大值
    private static final long MAX_MACHINE_ID = ~(-1L << MACHINE_ID_BITS);
    
    // 序列号最大值
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
    
    // 机器ID左移位数
    private static final long MACHINE_ID_SHIFT = SEQUENCE_BITS;
    
    // 时间戳左移位数
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS;
    
    // 机器ID
    private final long machineId;
    
    // 序列号
    private long sequence = 0L;
    
    // 上次生成ID的时间戳
    private long lastTimestamp = -1L;
    
    // 同步锁
    private final Object lock = new Object();
    
    /**
     * 构造函数
     * @param machineId 机器ID (0-1023)
     */
    public SnowflakeIdUtil(long machineId) {
        if (machineId > MAX_MACHINE_ID || machineId < 0) {
            throw new IllegalArgumentException(
                String.format("机器ID必须在0到%d之间", MAX_MACHINE_ID));
        }
        this.machineId = machineId;
    }
    
    /**
     * 默认构造函数，使用本机IP最后一段作为机器ID
     */
    public SnowflakeIdUtil() {
        this.machineId = getDefaultMachineId();
    }
    
    /**
     * 生成下一个ID
     * @return 雪花ID
     */
    public long nextId() {
        synchronized (lock) {
            long timestamp = getCurrentTimestamp();
            
            // 时钟回拨检查
            if (timestamp < lastTimestamp) {
                throw new RuntimeException(
                    String.format("时钟回拨，拒绝生成ID。当前时间戳：%d，上次时间戳：%d", 
                        timestamp, lastTimestamp));
            }
            
            // 同一毫秒内
            if (timestamp == lastTimestamp) {
                sequence = (sequence + 1) & MAX_SEQUENCE;
                // 序列号溢出，等待下一毫秒
                if (sequence == 0) {
                    timestamp = waitNextMillis(lastTimestamp);
                }
            } else {
                // 新的毫秒，序列号重置
                sequence = 0L;
            }
            
            lastTimestamp = timestamp;
            
            // 组装ID
            return ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                    | (machineId << MACHINE_ID_SHIFT)
                    | sequence;
        }
    }
    
    /**
     * 等待下一毫秒
     * @param lastTimestamp 上次时间戳
     * @return 新的时间戳
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }
    
    /**
     * 获取当前时间戳
     * @return 当前时间戳
     */
    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
    
    /**
     * 获取默认机器ID（基于本机IP）
     * @return 机器ID
     */
    private long getDefaultMachineId() {
        try {
            java.net.InetAddress addr = java.net.InetAddress.getLocalHost();
            String ip = addr.getHostAddress();
            String[] parts = ip.split("\\.");
            if (parts.length == 4) {
                // 使用IP最后两段作为机器ID
                long id = (Long.parseLong(parts[2]) << 8) + Long.parseLong(parts[3]);
                return id & MAX_MACHINE_ID;
            }
        } catch (Exception e) {
            // 获取IP失败，使用随机数
            return (long) (Math.random() * MAX_MACHINE_ID);
        }
        return 1L;
    }
    
    /**
     * 解析雪花ID
     * @param id 雪花ID
     * @return ID信息
     */
    public IdInfo parseId(long id) {
        long timestamp = (id >> TIMESTAMP_SHIFT) + START_TIMESTAMP;
        long machineId = (id >> MACHINE_ID_SHIFT) & MAX_MACHINE_ID;
        long sequence = id & MAX_SEQUENCE;
        
        return new IdInfo(timestamp, machineId, sequence);
    }
    
    /**
     * ID信息类
     */
    public static class IdInfo {
        private final long timestamp;
        private final long machineId;
        private final long sequence;
        
        public IdInfo(long timestamp, long machineId, long sequence) {
            this.timestamp = timestamp;
            this.machineId = machineId;
            this.sequence = sequence;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public long getMachineId() {
            return machineId;
        }
        
        public long getSequence() {
            return sequence;
        }
        
        @Override
        public String toString() {
            return String.format("IdInfo{timestamp=%d, machineId=%d, sequence=%d, time=%s}",
                timestamp, machineId, sequence, 
                new java.util.Date(timestamp).toString());
        }
    }
    
    /**
     * 静态方法，快速生成ID
     */
    private static volatile SnowflakeIdUtil instance;
    
    public static long generateId() {
        if (instance == null) {
            synchronized (SnowflakeIdUtil.class) {
                if (instance == null) {
                    instance = new SnowflakeIdUtil();
                }
            }
        }
        return instance.nextId();
    }
}