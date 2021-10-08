using System;
using System.ComponentModel;
using System.Diagnostics;
using System.Net.Sockets;
using System.Net;
using System.Text.Json.Serialization;
using System.IO;
using System.Text.Json;
using System.Threading;

namespace 进程管理器
{
    class Program
    {
        const string SERVER_IP = "127.0.0.1";
        const int SERVER_PORT = 8084;
        static int keepAliveNum = 0;

        public static int KillProcess(string[] processNames)//关闭线程
        {
            int closedProcess = 0;
            bool closedFlag = false;
            do
            {
                closedFlag = false;
                foreach (Process p in Process.GetProcesses())//GetProcessesByName(strProcessesByName))
                {
                    foreach (string processName in processNames)
                    {
                        if (p.ProcessName.ToUpper().Contains(processName.ToUpper()))
                        {
                            try
                            {
                                p.Kill();
                                p.WaitForExit(); // possibly with a timeout
                                closedProcess++;
                                closedFlag = true;
                                Console.WriteLine(p.ProcessName + " Closed");
                                break;

                            }
                            catch (Win32Exception e)
                            {
                                Console.WriteLine("Close " + p.ProcessName + " Failed");
                            }
                            catch (InvalidOperationException e)
                            {
                                Console.WriteLine("Close " + p.ProcessName + " Failed");
                            }
                        }
                    }
                    if (closedFlag == true)
                    {
                        break;
                    }
                }
            } while (closedFlag == true);
            return closedProcess;
        }
        static void Main(string[] args)
        {
            IPAddress address = IPAddress.Parse(SERVER_IP);
            IPEndPoint endPoint = new IPEndPoint(address, SERVER_PORT);

            while (true)
            {
                try{
                    Socket socket = new Socket(SocketType.Stream, ProtocolType.Tcp);
                    socket.Connect(endPoint);
                    //NetworkStream stream = new NetworkStream(socket, true);
                    Thread alive = new Thread(new ParameterizedThreadStart(keepAlive));
                    alive.Start(socket);
                    ListenReceive(socket);
                } 
                catch(SocketException e)
                {
                    Console.WriteLine(e.Message);
                }
                Console.WriteLine("retry connect...");
            }
        }
        private static void ListenReceive(Object obj)
        {
            Socket socket = (Socket)obj;
            while (true)
            {
                //todo：缓冲区拼接
                byte[] buffer = new byte[10240];
                Console.WriteLine("connect success!");
                int length = socket.Receive(buffer);
                if(length > 0)
                {
                    string json = System.Text.Encoding.UTF8.GetString(buffer, 0, length);
                    Console.WriteLine("receive: " + json);
                    SendDTO message = JsonSerializer.Deserialize<SendDTO>(json);
                    if(message.type.Equals(Type.KEEY_ALIVE))
                    {
                        keepAliveNum = 0;
                    }
                    else if (message.type.Equals(Type.EXEC))
                    {
                        Console.WriteLine("Closing " + message.gameName);
                        string[] gameNames = new string[] { message.gameName };
                        int resultCode = KillProcess(gameNames);
                        message.resultCode = resultCode;
                        message.type = Type.RESULT;
                        byte[] sendMessage = JsonSerializer.SerializeToUtf8Bytes(message);
                        socket.Send(sendMessage);
                        Console.WriteLine("Send " + resultCode);
                    }
                }
                else
                {
                    socket.Close();
                    break;
                }
            }

        }
        private static void keepAlive(Object socket)
        {
            while (true)
            {
                Thread.Sleep(30*1000);
                if (keepAliveNum >= 2)
                {
                    ((Socket)socket).Close();
                    keepAliveNum = 0;
                    break;
                }
                else
                {
                    keepAliveNum++;
                }
            }
        }
    }
}
