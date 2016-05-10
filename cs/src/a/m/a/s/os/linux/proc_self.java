package a.m.a.s.os.linux;

import java.io.File;

/*
 attr
 autogroup
 auxv
 cgroup
 clear_refs
 cmdline
 comm
 coredump_filter
 cpuset
 cwd
 environ
 exe
 fd
 fdinfo
 io
 limits
 maps
 mem
 mountinfo
 mounts
 mountstats
 net
 ns
 numa_maps
 oom_adj
 oom_score
 oom_score_adj
 pagemap
 personality
 root
 sched
 schedstat
 smaps
 stack
 stat
 statm
 status
 syscall
 task
 wchan
 */
public class proc_self extends proc {
	
	static final File proc_self_cmdline = new File("/proc/self/cmdline"); 
	public static String cmdline() {
		ReadLineListener l = new ReadLineListener() {
			@Override
			public boolean onHandleNewLine(int n, String l) {
				sb.append(l);
				return true;
			}
		};
		
		readline(proc_self_cmdline, l);
		return l.toString();
	}
	
	static final File proc_self_task = new File("/proc/self/task");  
	
}
