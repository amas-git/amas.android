package a.m.a.s.os.linux;

import java.io.File;

import a.m.a.s.os.linux.vnode.rules;

public class proc_pid_stat extends proc {
	static rules rule_stat = new rules();
	{
		rule_stat.notchanged("01 PID", Integer.class);
		rule_stat.notchanged("02 COMM");
		rule_stat.notchanged("03 STATE");
		rule_stat.notchanged("04 PPID");
		rule_stat.notchanged("05 PGRP");
		rule_stat.notchanged("06 SESSION");
		rule_stat.notchanged("07 TTY_NR");
		rule_stat.notchanged("08 TPGID");
		rule_stat.notchanged("09 FLAGS");
		rule_stat.notchanged("10 MINFLT");
		rule_stat.notchanged("11 CMINFLT");
		rule_stat.notchanged("12 MAJFLT");
		rule_stat.notchanged("13 CMAJFLT");
		rule_stat.notchanged("14 UTIME");
		rule_stat.notchanged("15 STIME");
		rule_stat.notchanged("16 CUTIME");
		rule_stat.notchanged("17 CSTIME");
		rule_stat.notchanged("18 PRIORITY");
		rule_stat.notchanged("19 NICE");
		rule_stat.notchanged("20 NUM_THREADS");
		rule_stat.notchanged(".ITREALVALUE");
		rule_stat.notchanged(".STARTTIME");
		rule_stat.notchanged(".VSIZE");
		rule_stat.notchanged(".RSS");
		rule_stat.notchanged(".RSSLIM");
		rule_stat.notchanged(".STARTCODE");
		rule_stat.notchanged(".ENDCODE");
		rule_stat.notchanged(".STARTSTACK");
		rule_stat.notchanged(".KSTKESP");
		rule_stat.notchanged(".KSTKEIP");
		rule_stat.notchanged(".SIGNAL");
		rule_stat.notchanged(".BLOCKED");
		rule_stat.notchanged(".SIGIGNORE");
		rule_stat.notchanged(".SIGCATCH");
		rule_stat.notchanged(".WCHAN");
		rule_stat.notchanged(".NSWAP");
		rule_stat.notchanged(".CNSWAP");
		rule_stat.notchanged(".EXIT_SIGNAL");
		rule_stat.notchanged(".PROCESSOR");
		rule_stat.notchanged(".RT_PRIORITY");
		rule_stat.notchanged(".POLICY");
		rule_stat.notchanged(".DELAYACCT_BLKIO_TICKS");
		rule_stat.notchanged(".GUEST_TIME");
		rule_stat.notchanged(".CGUEST_TIME");
		rule_stat.notchanged(".START_DATA");
		rule_stat.notchanged(".END_DATA");
		rule_stat.notchanged(".START_BRK");
		rule_stat.notchanged(".ARG_START");
		rule_stat.notchanged(".ARG_END");
		rule_stat.notchanged(".ENV_START");
		rule_stat.notchanged(".ENV_END");
		rule_stat.notchanged(".EXIT_CODE");
	}

	String pid = null;
	vnode node = null;
	
	public proc_pid_stat() {
		pid = "self";
	}
	
	public proc_pid_stat(final String pid) {
		if(pid != null) {
			return;
		}
		this.pid = pid;
	}
	
	public proc_pid_stat update() {
		node = vnode.createFromFile(rule_stat, new File("/proc/"+pid+"/stat"));
		return this;
	}
	
	@Override
	public String toString() {
		return node.toString();
	}
}
