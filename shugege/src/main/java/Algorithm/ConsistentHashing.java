package Algorithm;


import java.util.*;

/**
 * 一致性hash算法 用list集合加遍历实现 todo 演示动态扩容
 */
public class ConsistentHashing {
    private static String[] realNode={
            "192.168.0.1:8080",
            "192.168.0.2:8080",
            "192.168.0.3:8080"};

    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<>();
        Map<Integer, String> nodeMap= new HashMap<Integer, String>();
        for (int i = 0; i <realNode.length ; i++) {
           // System.out.println(realNode[i]+"的哈希值为 ："+realNode[i].hashCode());
            //因为hash值有可能为负，所以对计算出的hash值取绝对值
            //每个节点虚拟出五个节点ip#0 的格式
            for (int j = 0; j < 5 ; j++) {
                int abs = Math.abs((realNode[i]+"#"+j).hashCode());
                list.add(abs);
                nodeMap.put(abs,"Node"+realNode[i]+"#"+j);
            }
        }
        System.out.println("hash取值区间【0-2'32-1】，个数为： "+4294967296L);
        String[] nodes = {"127.0.0.1:8080", "192.168.0.1:8787", "192.168.0.2:54367"};
        for (int i = 0; i <nodes.length ; i++) {
            int code = nodes[i].hashCode();
            int abs = Math.abs(code);
            System.out.println(nodes[i]+":  "+abs);
            getNode(abs,list,nodeMap);

        }

    }

    /**
     * 顺时针找真实节点
     * @param abs 对象hash值
     * @param list 节点hash集合
     * @param nodeMap 节点名称
     */
    private static void getNode(int abs,ArrayList<Integer> list ,Map<Integer,String> nodeMap){
        int sub=0;
        String node="";
        for (int j:
             list) {

            //节点值比对象哈希值小的直接跳过
            if(j<=abs)continue;
            //节点与对象hash的差值
            int i  = j-abs;
            System.out.println("差值："+i+"  对应哈希值："+j+"   对应节点："+nodeMap.get(j));
            /**
             * sub为最小的差值，当i小于sub时，把i赋值给sub，最终node记录的ip则为请求路由到的目标服务器
             */
            if (i<sub&&sub!=0){
                sub=i;
                node=nodeMap.get(j);
            }else if (sub==0){ //第一次进入直接赋值
                sub=i;
                node=nodeMap.get(j);
            }
        //    System.out.println("i:  "+i+"      sub      "+sub);
        }
        System.out.println(abs+"    的最终虚拟节点为  "+node+"      真实节点为   ："+node.substring(0,node.indexOf("#")));
    }
}
/**
 * 不带虚拟节点的一致性Hash算法
 *
 */
class ConsistentHashingWithoutVirtualNode
{
    /**
     * 待添加入Hash环的服务器列表
     */
    private static String[] servers = {"192.168.0.0:111", "192.168.0.1:111", "192.168.0.2:111",
            "192.168.0.3:111", "192.168.0.4:111"};

    /**
     * key表示服务器的hash值，value表示服务器的名称
     */
    private static SortedMap<Integer, String> sortedMap =
            new TreeMap<Integer, String>();

    /**
     * 程序初始化，将所有的服务器放入sortedMap中
     */
    static
    {
        for (int i = 0; i < servers.length; i++)
        {
            int hash = getHash(servers[i]);
            System.out.println("[" + servers[i] + "]加入集合中, 其Hash值为" + hash);
            sortedMap.put(hash, servers[i]);
        }
        System.out.println();
    }

    /**
     * 使用FNV1_32_HASH算法计算服务器的Hash值,这里不使用重写hashCode的方法，最终效果没区别
     */
    private static int getHash(String str)
    {
        final int p = 16777619;
        int hash = (int)2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 如果算出来的值为负数则取其绝对值
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }

    /**
     * 得到应当路由到的结点
     */
    private static String getServer(String node)
    {
        // 得到带路由的结点的Hash值
        int hash = getHash(node);
        // 得到大于该Hash值的所有Map
        SortedMap<Integer, String> subMap =
                sortedMap.tailMap(hash);
        // 第一个Key就是顺时针过去离node最近的那个结点
        Integer i = subMap.firstKey();
        // 返回对应的服务器名称
        return subMap.get(i);
    }

    public static void main(String[] args)
    {
        String[] nodes = {"127.0.0.1:1111", "221.226.0.1:2222", "10.211.0.1:3333"};
        for (int i = 0; i < nodes.length; i++)
            System.out.println("[" + nodes[i] + "]的hash值为" +
                    getHash(nodes[i]) + ", 被路由到结点[" + getServer(nodes[i]) + "]");
    }
}




/**
 * shugege 一致性hash算法的实现 ，服务器的动态扩容以及缓存命中率
 */
class ConsistentHashingWithVirtualNode {
    private static ArrayList<String> servers= new ArrayList<>();

    private static SortedMap<Integer,String> mapVirtualNodes= new TreeMap<>();
    private static Integer virtualNodes= 10;

    static {
        servers.add("192.168.0.1:8080");
        servers.add("192.168.0.2:8080");
        servers.add("192.168.0.3:8080");
        for (int i = 0; i <servers.size() ; i++) {
            for (int j = 0; j <virtualNodes ; j++) {
                String virtualNodesIP = servers.get(i) + "#" + j;
                mapVirtualNodes.put(getHashCode(virtualNodesIP), virtualNodesIP);
            }
        }
    }

    /**
     * 获取hash值
     * @param str
     * @return
     */
    private static Integer getHashCode(String str){

        return Objects.hash(str);
    }

    /**
     *
     * @param requestIP 请求IP
     * @return
     */
    private static String getNode(String requestIP){
        SortedMap<Integer, String> tailMap = mapVirtualNodes.tailMap(getHashCode(requestIP));
        Integer rightCode = tailMap.firstKey();
        String rightVirtualCode = tailMap.get(rightCode);
        return rightVirtualCode.substring(0,rightVirtualCode.indexOf("#"));

    }

    public static void main(String[] args) {
        String [] nodes= new String[]{"127.0.0.1:8080","127.0.0.1:1111", "221.226.0.1:2222", "10.211.0.1:3333"};
        for (int i = 0; i <nodes.length ; i++) {
            System.out.println(getNode(nodes[i]));
        }
    }

}
