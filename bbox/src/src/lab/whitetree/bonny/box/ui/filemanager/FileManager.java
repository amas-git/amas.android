package lab.whitetree.bonny.box.ui.filemanager;

import java.io.File;

import lab.whitetree.bonny.box.R;
import lab.whitetree.bonny.box.ui.BaseListActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple file manager to help user manage file/dir on sdcard
 * @author amas
 * TODO: move the io operations to backgraound
 */
public class FileManager extends BaseListActivity {
	private static final String TARGET_DIR  = ":target-dir";
	private static final String DEFAULT_TOP_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
	
	private static final int DLG_ID_DELETE      = 1;
	
	FileSystemNode  mNode           = new FileSystemNode(DEFAULT_TOP_DIR);
	String mStartPath               = null;
	
	FileSystemNode  mHighlightNode  = null; // for copy , move, paste
	boolean        mRmAfterCopy    = false;
	
	TextView        mTvMsg          = null;
	ImageView       mIvIcon         = null;
	LinearLayout    mNotifyPanel    = null;
	LinearLayout    mOptionPanel    = null;
	Button          mBtnNew         = null;
	TextView        mPathInfo       = null;
	
	/**
	 * Start file manager
	 * @param context
	 */
	public static void startFileManager(Context context) {
		Intent intent = new Intent(context, FileManager.class);
		context.startActivity(intent);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
	}
	
	public static void startFileManager(Context context, String targetDir) {
		Intent intent = getLaunchIntent(context);
		intent.putExtra(TARGET_DIR, targetDir);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		context.startActivity(intent);
	}
	
	/**
	 * Get default launch intent
	 * @param context
	 * @return
	 */
	public static Intent getLaunchIntent(Context context) {
		return new Intent(context, FileManager.class);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fmanager_main);

		
		Intent intent = getIntent();
		
		if(intent.hasExtra(TARGET_DIR)) {
			mStartPath = intent.getStringExtra(TARGET_DIR);
			mNode = new FileSystemNode(mStartPath);
		}
		
		getListView().setAdapter(createFileListAdapter(mNode.getAbsolutePath()));
		registerForContextMenu(getListView());
		
		mNotifyPanel = (LinearLayout)findViewById(R.id.notify_panel);
		mOptionPanel = (LinearLayout)findViewById(R.id.option_panel);
		//mBtnNew      = (Button)findViewById(R.id.btn_newdir);
//		mBtnNew.setOnClickListener(new View.OnClickListener() {		
//			public void onClick(View v) {
//				onCreateFolder(mNode);
//			}
//		});
		mPathInfo    = (TextView)findViewById(R.id.et_path);
		mPathInfo.setText(mNode.getAbsolutePath());
		updateNotifyPanel();
	}
	
	private void onCreateFolder(FileSystemNode node) {
		//createNewFolderDialog(node).show();
	}
	
	/**
	 * The notify panel only set to be visiable when the current node has no child.
	 */
	private void updateNotifyPanel() {
//		if(mNode.getChildren().size() > 0) {
//			getListView().setVisibility(View.VISIBLE);
//			mNotifyPanel.setVisibility(View.GONE);
//		} else {
//			mNotifyPanel.setVisibility(View.VISIBLE);
//			mTvMsg = (TextView)findViewById(R.id.tv_msg);
//			if(isTopNode()) {
//				mOptionPanel.setVisibility(View.GONE);
//				mTvMsg.setText(R.string.no_sdcard);
//			} else {
//				mOptionPanel.setVisibility(View.VISIBLE);
//				mTvMsg.setText(R.string.empty_dir);
//			}
//		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.fmanger_ctx_menu, menu); 
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
		FileSystemNode node = (FileSystemNode)(getFileListAdapter().getItem(info.position));
		// if the highlight node has deleted by user, clear it first.
		if(mHighlightNode != null && !mHighlightNode.existed()) {
			mHighlightNode = null;
		}
		
		// avoid copying to itself
//		if(node.equals(mHighlightNode)) {
//			menu.removeItem(R.id.ctxmid_copy);
//			menu.removeItem(R.id.ctxmid_cut);
//		}
//		
//		// toggle paste menu
//		if(mHighlightNode != null && mHighlightNode.getParent().getAbsolutePath().equals(node.getAbsolutePath())) {
//			menu.removeItem(R.id.ctxmid_paste);
//		}
//		
//		if(mHighlightNode == null || !node.isDirectory()) {
//			menu.removeItem(R.id.ctxmid_paste);
//		}
//		
//		if(!node.isDirectory()) {
//			menu.removeItem(R.id.ctxmid_new_folder);
//		} else {
//			menu.removeItem(R.id.ctxmid_share);
//		}
		// avoid move to itself
	}
	
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		FileSystemNode node = (FileSystemNode)(getFileListAdapter().getItem(info.position));
		
		switch (item.getItemId()) {
//		case R.id.ctxmid_copy:
//			//onCopy(node);
//			break;
		case R.id.ctxmid_delete:
			onDelete(node);
			break;
//		case R.id.ctxmid_cut:
//			onCut(node);
//			break;
//		case R.id.ctxmid_paste:
//			//onPaste(node);
//			break;
//		case R.id.ctxmid_rename:
//			//onRename(node);
//			break;
//		case R.id.ctxmid_new_folder:
//			onCreateFolder(node);
//			break;
//		case R.id.ctxmid_share:
//			//onShare(node);
//			break;
		}
		return true;
	}
	
	private void onCut(FileSystemNode node) {
		mHighlightNode = node;
		mRmAfterCopy   = true;
		//warn(String.format(getString(R.string.warn_fmt_file_cut),node.getName()));
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case DLG_ID_DELETE:
            return new AlertDialog.Builder(this)
            //.setTitle(R.string.delete)
            .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            })
            .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            })
            .create();
       default:
    	   /* NOP */
		}
		return null;
	}
	
	private FileListAdapter getFileListAdapter() {
		return (FileListAdapter)getListView().getAdapter();
	}
	/**
	 * Make a short toast to show some messsage
	 * @param msg
	 * @return
	 */
	private void warn(String msg) {
		Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();	
	}
	
//	private void onRename(FileSystemNode node) {
//		createRenameDialog(node).show();
//	}
//
//	private void onPaste(FileSystemNode node) {
//		createPasteDialog(node).show();
//	}

//	private void onMove(FileSystemNode node) {
////		createMoveDialog(node).show();
//	}

	private void onDelete(FileSystemNode node) {
		AlertDialog dlg  = createDeleteDialog(node);
		dlg.show();
	}
	
//	private void onShare(FileSystemNode node) {
//		Intent it = new Intent(Intent.ACTION_SEND);   
//		it.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share));   
//		it.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(node.asFile()));   
//		it.setType(node.getMimeInfo().getMimeType());   
//		startActivity(Intent.createChooser(it, getString(R.string.share)));
//	}
	
	/**
	 * delete file alert dialog
	 * @param node
	 * @return
	 */
	private AlertDialog createDeleteDialog(final FileSystemNode node) {
        return new AlertDialog.Builder(this)
        .setTitle(R.string.delete)
        .setMessage(String.format(getString(R.string.warn_delete_file), node.getAbsolutePath()))
        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	if(node.delete()) {
            		(getFileListAdapter()).remove(node, true);
            	}
            }
        })
        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	
            }
        })
        .create();
	}
	
	
	/**
	 * Create renaming dialog
	 * @param node
	 * @return
	 */
//	private AlertDialog createRenameDialog(final FileSystemNode node) {
//        LayoutInflater factory = LayoutInflater.from(this);
//        final View view = factory.inflate(R.layout.alert_dialog_text_entry, null);
//        final String parentPath = node.getParent().getAbsolutePath();
//        TextView note = (TextView)view.findViewById(id.tv_note);
//        note.setText(R.string.msg_input_filename);
//        final EditText edit = (EditText)view.findViewById(id.ed_text);
//        edit.setText(node.getName());
//        
//        return new AlertDialog.Builder(this)
//            .setTitle(R.string.rename)
//            .setIcon(R.drawable.edit)
//            .setView(view)
//            .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                		String  newName = normalizeFileName(edit.getText().toString().trim());
//                		if(new File(parentPath+"/"+newName).exists()) {
//                			warn(getString(R.string.existed_file));
//                			return;
//                		}              		
//                		boolean success = getFileListAdapter().rename(node,newName);
//                		if(success) {
//                			warn(String.format(getString(R.string.warn_rename_done), newName));
//                		} else {
//                			warn(getString(R.string.warn_rename_error));
//                		}
//                }
//            })
//            .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                		/* NOP */
//                }
//            })
//            .create();
//	}
	
//	private AlertDialog createNewFolderDialog(final FileSystemNode parentNode) {
//        LayoutInflater factory = LayoutInflater.from(this);
//        final View view = factory.inflate(R.layout.alert_dialog_text_entry, null);
//        final String parentPath = parentNode.getAbsolutePath(); 
//        TextView note = (TextView)view.findViewById(id.tv_note);
//        note.setText(R.string.msg_input_dirname);
//        final EditText edit = (EditText)view.findViewById(id.ed_text);
//        
//        return new AlertDialog.Builder(this)
//            .setTitle(R.string.new_dir)
//            .setIcon(R.drawable.edit)
//            .setView(view)
//            .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                		String newDir = edit.getText().toString().trim();
//                		
//                		if(new File(parentPath +"/"+ newDir).exists()) {
//                			warn(getString(R.string.existed_file));
//                			return;
//                		}  
//                		
//                		if(TextUtils.isEmpty(newDir)) {
//                			warn(getString(R.string.please_enter_dir_name));
//                		} else {
//                			if(createNewDir(parentNode, newDir)) {
//                				warn(String.format(getString(R.string.fmt_created_dir), newDir));
//                			} else {
//                				
//                			}
//                		}
//                }
//            })
//            .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                		/* NOP */
//                }
//            })
//            .create();
//	}
	
	private boolean createNewDir(FileSystemNode parent, String path) {
		FileSystemNode newDir = parent.mkdir(path);
		if(newDir != null) {
			getFileListAdapter().addItem(newDir);
			updateNotifyPanel(); 
			return true;
		}
		return false;
	}
	
	private FileSystemNode getHighlightNode() {
		return mHighlightNode;
	}
	
	private void clearHighlightNode() {
		mHighlightNode = null;
	}
	
//	private AlertDialog createPasteDialog(final FileSystemNode node) {
//        return new AlertDialog.Builder(this)
//        .setTitle(R.string.paste)
//        .setMessage(String.format(getString(R.string.dlg_warn_paste_file), mHighlightNode.getAbsolutePath(), node.getAbsolutePath()))
//        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//            	try {
//					FileSystemNode.copyNode(getHighlightNode(), node);
//				} catch (IOException e) {
//					warn(getString(R.string.warn_pasted_error));
//				}
//				if(mRmAfterCopy) {
//					getFileListAdapter().remove(getHighlightNode(), true);
//				}
//				clearHighlightNode();
//				warn(getString(R.string.warn_pasted_done));
//            }
//
//        })
//        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//
//            }
//        })
//        .create();
//	}
	
//	private AlertDialog createMoveDialog(final FileSystemNode node) {
//        LayoutInflater factory = LayoutInflater.from(this);
//        final View view = factory.inflate(R.layout.alert_dialog_text_entry, null);
//        
//        TextView note = (TextView)view.findViewById(id.tv_note);
//        note.setText(R.string.msg_input_dirname);
//        final EditText edit = (EditText)view.findViewById(id.ed_text);
//        edit.setText(node.getParent().getAbsolutePath());
//        
//        return new AlertDialog.Builder(this)
//            .setTitle(R.string.move)
//            .setIcon(R.drawable.edit)
//            .setView(view)
//            .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                		String  newPath = normalizeFileName(edit.getText().toString().trim());
//                		int success = 0;
//						try {
//							success = FileSystemNode.copyNode(node, new FileSystemNode(newPath));
//						} catch (IOException e) {
//							success = -1;
//						}
//						
//                		if(success == 0) {
//                			//getFileListAdapter().remove(node, true);
//                			warn(getString(R.string.warn_moved_done));
//                		} else {
//                			warn(getString(R.string.warn_moved_error));
//                		}
//                }
//            })
//            .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                		/* NOP */
//                }
//            })
//            .create();
//	}
	/**
	 * Remove the validate chars of the given file name
	 * @param name
	 * @return
	 */
	private static String normalizeFileName(String name) { 
		return name.replaceAll("[\r\n\t/]", "");
	}

//	private void onCopy(FileSystemNode node) {
//		mHighlightNode = node;
//		mRmAfterCopy   = false;
//		warn(String.format(getString(R.string.warn_fmt_file_copyed),node.getName()));
//	}

	public FileListAdapter createFileListAdapter(String path) {
		FileSystemNode node = new FileSystemNode(path);
		return new FileListAdapter(this, node.getChildren());
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		FileSystemNode node = (FileSystemNode)(getListView().getAdapter().getItem(position)); 
		if(node.isDirectory()) {
			onClickDirectory(node);
		} else {
			onClickFile(node);
		}
	}
	
	public void onClickDirectory(FileSystemNode node) {
		selectDirNode(node);
	}
	
	public void onClickFile(FileSystemNode node) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(node.getAbsolutePath())),
				node.getMimeInfo().getMimeType());
		try {
			startActivity(intent);
		} catch(Exception e) {
			//TODO: warn a message
			warn(getString(R.string.open_failed));
		}
	}
	
	/**
	 * Change current node, refresh ui
	 * @param targetDir
	 */
	private void selectDirNode(FileSystemNode targetDir) {
		if(targetDir.getAbsolutePath().equals("/")) {
			finish();
		}
		mNode = targetDir;
		getListView().setAdapter(createFileListAdapter(targetDir.getAbsolutePath()));
		mPathInfo.setText(mNode.getAbsolutePath());
		updateNotifyPanel();
	}
	
	/**
	 * If current node is top node then press back key will exit the application
	 * @return true if the current node is top node
	 */
	private boolean isTopNode() {
		return mStartPath.equals(mNode.getAbsolutePath());
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && !isTopNode()) {
			selectDirNode(mNode.getParent());
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}

