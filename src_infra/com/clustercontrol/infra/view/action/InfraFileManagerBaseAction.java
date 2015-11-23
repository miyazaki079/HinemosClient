package com.clustercontrol.infra.view.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

import com.clustercontrol.infra.action.GetInfraFileManagerTableDefine;
import com.clustercontrol.infra.view.InfraFileManagerView;

public abstract class InfraFileManagerBaseAction extends AbstractHandler
		implements IElementUpdater {

	protected IWorkbenchWindow window;

	/** ビュー */
	protected IWorkbenchPart viewPart;

	public InfraFileManagerBaseAction() {
		super();
	}

	/**
	 * Dispose
	 */
	@Override
	public void dispose() {
		this.viewPart = null;
		this.window = null;
	}

	@Override
	public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
		boolean enable = false;
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		// page may not start at state restoring
		if( null != window ){
			IWorkbenchPage page = window.getActivePage();
			if( null != page ){
				IWorkbenchPart part = page.getActivePart();

				if (part instanceof InfraFileManagerView) {
					InfraFileManagerView view = (InfraFileManagerView) part.getAdapter(InfraFileManagerView.class);

					StructuredSelection selection = null;
					if (view.getComposite().getTableViewer().getSelection() instanceof StructuredSelection) {
						selection = (StructuredSelection) view.getComposite().getTableViewer().getSelection();
					}

					if (selection != null && selection.size() == 1) {
						enable = true;
					}
				}
				this.setBaseEnabled(enable);
			}
		}
	}

	protected InfraFileManagerView getView(ExecutionEvent event) {
		this.window = HandlerUtil.getActiveWorkbenchWindow(event);
		if( null == this.window || !isEnabled() ){
			return null;
		}

		this.viewPart = HandlerUtil.getActivePart(event);

		InfraFileManagerView view = null;
		if(viewPart instanceof InfraFileManagerView){
			view = (InfraFileManagerView) viewPart.getAdapter(InfraFileManagerView.class);
		}
		return view;
	}

	protected List<String> getSelectedInfraFileIdList(InfraFileManagerView view) {
		StructuredSelection selection = null;
		if(view.getComposite().getTableViewer().getSelection() instanceof StructuredSelection){
			selection = (StructuredSelection) view.getComposite().getTableViewer().getSelection();
		}

		List<String> fileIds = new ArrayList<String>();
		if(selection != null){
			for(Object object: selection.toList()){
				String fileId = (String) ((ArrayList<?>)object).get(GetInfraFileManagerTableDefine.FILE_ID);
				fileIds.add(fileId);
			}
		}
		return fileIds;
	}
}