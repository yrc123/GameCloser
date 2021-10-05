using System;
using System.ComponentModel;
using System.Diagnostics;
using System.Net;
using System.Net.Sockets;

namespace 进程管理器
{
    class Program
    {
        const string SERVER_IP = "127.0.0.1";
        const int SERVER_PORT = 8084;

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
                                //MessageBox.Show(e.Message.ToString());   // process was terminating or can't be terminated - deal with it
                            }
                            catch (InvalidOperationException e)
                            {
                                Console.WriteLine("Close " + p.ProcessName + " Failed");
                                //MessageBox.Show(e.Message.ToString()); // process has already exited - might be able to let this one go
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
                    Socket socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                    socket.Connect(endPoint);
                    ListenReceive(socket);
                } 
                catch(SocketException e)
                {
                    Console.WriteLine(e.Message);
                }
                Console.WriteLine("retry connect...");
                //Thread client = new Thread(new ParameterizedThreadStart(ListenReceive));
                //client.IsBackground = false;
                //client.Start(socket);
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
                    string gameName = System.Text.Encoding.UTF8.GetString(buffer, 0, length);
                    Console.WriteLine("Closing " + gameName);
                    string[] gameNames = new string[] { gameName };
                    int resultCode = KillProcess(gameNames);
                    byte[] sendMessage = System.Text.Encoding.UTF8.GetBytes(resultCode.ToString()+"\n");
                    socket.Send(sendMessage);
                    Console.WriteLine("Send " + resultCode);
                }
                else
                {
                    socket.Close();
                    break;
                }
            }

        }
    }
}
