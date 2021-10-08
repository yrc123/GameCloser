using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace 进程管理器
{
    class SendDTO
    {
        public Type type { get; set; }
        public string hostname { get; set; }
        public string guid { get; set; }
        public int resultCode { get; set; }
        public string gameName { get; set; }
    }
    enum Type
    {
        KEEY_ALIVE = 1,
        EXEC = 2,
        RESULT = 3
    }
}
