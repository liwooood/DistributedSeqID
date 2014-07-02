package com.cssweb.distributedseqid.impl;

/**
 * Created by chenhf on 2014/7/2.
 */
public class SnowflakeImpl {
    //https://github.com/twitter/snowflake/

    /*
    由时间戳 + 节点号 + 序列编号组成
    节点号由数据中心id + 节点id组成

    符号位0 + 41位毫秒数时间戳 + （5位数据中心id + 5位节点id) + 12位自增值
    1 + 41 + 10 + 12 = 64位 (long)
    序列编号有 12 位，意味着 【每个节点】 在 【每毫秒】 可以产生  【4096 个 ID】
*/
    private final long workerId; // 机器标识

    //1303895660503L
    //1288834974657L
    //1361753741828L
    private final static long twepoch = 1303895660503L;

    private long sequence = 0L;

    private final static long workerIdBits = 10L; // 机器标识位数

    public final static long maxWorkerId = -1L ^ (-1L << workerIdBits); // 机器id最大值

    private final static long sequenceBits = 12L; // 毫秒内自增位数

    private final static long workerIdShift = sequenceBits; // 节点id左移12位

    private final static long timestampLeftShift = sequenceBits + workerIdBits; // 时间毫秒左移22位

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
            // 当前毫秒内， 则seq + 1
            this.sequence = (this.sequence + 1) & this.sequenceMask;

            if (this.sequence == 0) {
                // 当前毫秒内计数满了，则等待下一秒
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
        Long t = 1303895660503L;
        System.out.println(Long.toBinaryString(t));

        SnowflakeImpl worker2 = new SnowflakeImpl(10);
        for (int i=0; i<1; i++) {
            Long nextId = worker2.nextId();
            String id = nextId.toString();
            System.out.println("nextid = " + id + ", len=" + id.length());
        }
    }
}
