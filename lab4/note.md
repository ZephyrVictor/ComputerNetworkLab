# 注意

这个实验中判断ip头部仅仅判断是否是等于20，因为当时助教写平台就是这么写的，尽管在ipv4协议中最小头部长度是20。

因此在考虑此项目时应当注意测试平台的具体要求

另外，笔者在使用pcap包时，发现有些包会被java识别为空（因为ipv4头部过小），所以简单粗暴地在处理文件的逻辑判断中直接抛出异常以求过关。