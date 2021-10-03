using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace 进程管理器
{
    class Program
    {
        static string[] PROCESS_NAMES = { "UUVoice" , "YY" };
        //static int MAX_SLEEP_TIME = 10 * 60 * 1000;
        //static int MIN_SLEEP_TIME = 5 * 60 * 1000;
        static int MAX_SLEEP_TIME = 5000;
        static int MIN_SLEEP_TIME = 3000;
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
            Random random = new Random();
            for (; ; )
            {
                KillProcess(PROCESS_NAMES);
                Thread.Sleep(random.Next(MIN_SLEEP_TIME, MAX_SLEEP_TIME));
            }

        }
    }
}
