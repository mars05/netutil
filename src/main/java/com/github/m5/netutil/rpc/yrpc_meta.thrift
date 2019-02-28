namespace c_glib com.github.m5.netutil.rpc.thrift
namespace java com.github.m5.netutil.rpc.thrift
namespace cpp com.github.m5.netutil.rpc.thrift
namespace js ThriftTest
namespace py ThriftTest
namespace go thrifttest
namespace php ThriftTest
//namespace lua ThriftTest

struct YrpcRequest {
   1: string request_id,
   2: string group,
   3: string version,
   4: string interface_name,
   5: string method_name,
   6: list<binary> params,
}

struct YrpcResponse {
    1: string request_id,
    2: binary result,
    3: string err_msg,
}
//执行命令: ..\util\thrift-0.11.0.exe -gen java yrpc_meta.thrift