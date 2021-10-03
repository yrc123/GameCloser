using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace 进程管理器
{
    class Program
    {
        const string SERVER_IP = "127.0.0.1";
        const int SERVER_PORT = 4444;

        public static void KillProcess(string[] processNames)//关闭线程
        {
            foreach (Process p in Process.GetProcesses())//GetProcessesByName(strProcessesByName))
            {
                bool closedFlag = false;
                foreach (string processName in processNames)
                {
                    if (p.ProcessName.ToUpper().Contains(processName.ToUpper()))
                    {
                        try
                        {
                            Console.WriteLine("close: " + p.ProcessName);
                            p.Kill();
                            p.WaitForExit(); // possibly with a timeout
                            closedFlag = true;
                            break;

                        }
                        catch (Win32Exception e)
                        {
                            //MessageBox.Show(e.Message.ToString());   // process was terminating or can't be terminated - deal with it
                        }
                        catch (InvalidOperationException e)
                        {
                            //MessageBox.Show(e.Message.ToString()); // process has already exited - might be able to let this one go
                        }
                    }
                }
                if (closedFlag == true)
                {
                    break;
                }

            }
        }
        static void Main(string[] args)
        {
            Socket socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            IPAddress address = IPAddress.Parse(SERVER_IP);
            IPEndPoint endPoint = new IPEndPoint(address, SERVER_PORT);
            socket.Connect(endPoint);
            Thread client = new Thread(new ParameterizedThreadStart(ListenReceive));
            client.IsBackground = false;
            client.Start(socket);
        }
        private static void ListenReceive(Object obj)
        {
            Socket socket = (Socket) obj;
            byte[] buffer = new byte[10240];
            Console.WriteLine("connect success!");
            int length = socket.Receive(buffer);
            string gameName = System.Text.Encoding.UTF8.GetString(buffer,0,length);
            Console.WriteLine("Closing " + gameName);
            Console.ReadLine();
        }
    }
}
