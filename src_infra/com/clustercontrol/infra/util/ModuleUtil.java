package com.clustercontrol.infra.util;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;

import com.clustercontrol.dialog.TextAreaDialog;
import com.clustercontrol.infra.bean.ModuleTypeConstant;
import com.clustercontrol.infra.bean.OkNgConstant;
import com.clustercontrol.infra.bean.RunCheckTypeConstant;
import com.clustercontrol.infra.dialog.FileDiffDialog;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.infra.ModuleNodeResult;
import com.clustercontrol.ws.infra.ModuleResult;

public class ModuleUtil {
	private static Log m_log = LogFactory.getLog( ModuleUtil.class );

	/**
	 * 
	 * @param moduleId
	 * @param moduleResult
	 * @return
	 */
	public static boolean displayResult(String moduleId, ModuleResult moduleResult) {
		boolean ret = false; // trueになったら、以降実行しない。
		List<ModuleNodeResult> resultList = moduleResult.getModuleNodeResultList();
		String str = "";
		if (resultList.size() > 0) {
			if (moduleResult.getModuleType() == ModuleTypeConstant.TYPE_COMMAND) {
				// コマンドモジュール
				str = "moduleId=" + moduleId + ", size=" + resultList.size() +
						", type=" + ModuleTypeConstant.typeToString(moduleResult.getModuleType()) + "\n";
				for (ModuleNodeResult result : resultList) {
					str += "\n#####" + result.getFacilityId() + " : " +
							OkNgConstant.typeToString(result.getResult()) +
							"(" + RunCheckTypeConstant.typeToString(result.getRunCheckType()) + ")" +
							" \n" + result.getMessage() + " \n";
				}
				m_log.debug("checkModule : " + str);
			} else if (moduleResult.getModuleType() == ModuleTypeConstant.TYPE_FILETRANSFER){
				// ファイル転送モジュール
				for (ModuleNodeResult result : resultList) {
					if (result.getRunCheckType() == RunCheckTypeConstant.TYPE_CHECK) {
						if (result.isFileDiscarded()) {
							// ファイルサイズオーバーの場合はメッセージを表示して比較ダイアログを表示しない
							MessageDialog.openInformation(null, Messages.getString("message"), Messages.getString("message.infra.file.discarded"));
						} else {
							FileDiffDialog diffDialog = new FileDiffDialog(null);
							diffDialog.setTitle(Messages.getString("dialog.infra.title.diff", new Object[]{result.getFacilityId()}));
							if (result.getOldFilename() != null) {
								diffDialog.setOldFilename(result.getOldFilename());
							}
							if (result.getOldFile() != null) {
								diffDialog.setOldFile(result.getOldFile());
							}
							if (result.getNewFilename() != null) {
								diffDialog.setNewFilename(result.getNewFilename());
							}
							if (result.getNewFile() != null) {
								diffDialog.setNewFile(result.getNewFile());
							}
							m_log.debug("newFilename=" + result.getNewFilename());
							if (diffDialog.open() != IDialogConstants.OK_ID) {
								break;
							}
						}
					}
				}
				str = "moduleId=" + moduleId + ", size=" + resultList.size() +
						", type=" + ModuleTypeConstant.typeToString(moduleResult.getModuleType()) + "\n";
				for (ModuleNodeResult result : resultList) {
					str += "\n#####" + result.getFacilityId() + " : " +
							OkNgConstant.typeToString(result.getResult()) +
							"(" + RunCheckTypeConstant.typeToString(result.getRunCheckType()) + ")" +
							" \n" + result.getMessage() + " \n";
					if (result.getOldFile() != null) {
						str += ", oldFile.size=" + result.getOldFile().length;
					}
					if (result.getNewFile() != null) {
						str += ", newFile.size=" + result.getNewFile().length;
					}
					str += "\n";
				}
			}
			TextAreaDialog dialog = new TextAreaDialog(null, Messages.getString("dialog.infra.result"), true, false); 
			dialog.setText(str);
			dialog.setOkButtonText(Messages.getString("next"));
			dialog.setCancelButtonText(Messages.getString("suspend"));
			if (dialog.open() == IDialogConstants.OK_ID) {
				ret = true;
			}
		}
		return ret;
	}
}
