package com.cssweb.distributedseqid.impl;

/**
 * Created by chenhf on 2014/7/2.
 */
public class SnowflakeImpl {
    //https://github.com/twitter/snowflake/

    // 41位毫秒数时间戳 + （5位数据中心id + 5位节点id) + 12位自增值

    private final long workerId; // 机器标识

    //1303895660503L
    //1288834974657L
    //1361753741828L
    private final static long twepoch = 1288834974657L;

    private long sequence = 0L;

    private final static long workerIdBits = 10L; // 机器标识位数

    public final static long maxWorkerId = -1L ^ (-1L << workerIdBits); // 机器id最大值

    private final static long sequenceBits = 12L; // 毫秒内自增位数

    private final static long workerIdShift = sequenceBits;

    private final static long timestampLeftShift = sequenceBits + workerIdBits;

    public final static long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long lastTimestamp = -1L;


    public SnowflakeImpl(long workerId) {

        if (workerId > this.maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format(
                    "worker Id can't be greater than %d or less than 0",
                    this.maxWorkerId));
        }
        this.workerId = workerId;
    }

    public synchronized long nextId() {
        long timestamp = this.timeGen();

        // 把时间往前改了
        if (timestamp < this.lastTimestamp) {
            try {
                throw new Exception(
                        String.format(
                                "Clock moved backwards.  Refusing to generate id for %d milliseconds",
                                this.lastTimestamp - timestamp));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (this.lastTimestamp == timestamp) {
            this.sequence = (this.sequence + 1) & this.sequenceMask;

            if (this.sequence == 0) {
                timestamp = this.tilNextMillis(this.lastTimestamp);
            }
        } else {
            this.sequence = 0;
        }


        this.lastTimestamp = timestamp;
        long nextId = ((timestamp - twepoch) << timestampLeftShift) | (this.workerId << this.workerIdShift) | (this.sequence);
        return nextId;
    }

    /**
     * 得到下一个毫秒
     * @param lastTimestamp
     * @return
     */
    private long tilNextMillis(final long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    /**
     * 当前毫秒
     * @return
     */
    private long timeGen() {
        return System.currentTimeMillis();
        //return System.nanoTime();
    }


    public static void main(String[] args){
        SnowflakeImpl worker2 = new SnowflakeImpl(10);
        for (int i=0; i<10; i++) {
            System.out.println(worker2.nextId());
        }
    }
}
