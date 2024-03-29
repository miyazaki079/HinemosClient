package com.clustercontrol.jobmanagement.viewer;

import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

import com.clustercontrol.ws.jobmanagement.JobTreeItem;

public class JobTreeViewer extends TreeViewer{
	// ログ
	private static Log m_log = LogFactory.getLog( JobTreeViewer.class );

	public JobTreeViewer(Composite parent) {
		super(parent);
	}

	public JobTreeViewer(Tree parent) {
		super(parent);
	}

	public void sort (JobTreeItem item) {
		if (item == null) {
			return;
		}
		m_log.info("sort item=" + item.getData().getId());
		Collections.sort(item.getChildren(), new DataComparator());
	}

	private class DataComparator implements java.util.Comparator<JobTreeItem>{
		@Override
		public int compare(JobTreeItem o1, JobTreeItem o2){
			String s1 = o1.getData().getId();
			String s2 = o2.getData().getId();
			m_log.debug("s1=" + s1 + ", s2=" + s2);
			return s1.compareTo(s2);
		}
	}
}
