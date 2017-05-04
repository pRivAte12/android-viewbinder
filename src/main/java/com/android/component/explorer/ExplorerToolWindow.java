package com.android.component.explorer;

import com.android.component.explorer.manager.UnitManager;
import com.android.component.explorer.scanner.DirExplorer;
import com.android.component.explorer.scanner.FileHandler;
import com.android.component.explorer.scanner.Filter;
import com.android.component.explorer.unit.ActivityUnit;
import com.android.component.explorer.unit.ComponentUnit;
import com.android.component.explorer.unit.FragmentUnit;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.awt.event.*;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by parkjaesung on 2017. 4. 10..
 */
public class ExplorerToolWindow implements ToolWindowFactory {
    private JButton button1;
    private JPanel MainPanel2;
    private JLabel Label1;
    private JTree tree;
    private JTable table1;

    private HashMap<String, ActivityUnit> activityMap;
    private HashMap<String, FragmentUnit> fragmentMap;

    UnitManager unitManager = UnitManager.getInstance();

    public void createToolWindowContent(@NotNull final Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        System.out.println("creatToolWindow");
        Content content = contentFactory.createContent(MainPanel2, "",false);
        toolWindow.getContentManager().addContent(content);

        final VirtualFile[] vFiles = ProjectRootManager.getInstance(project).getContentRoots();
        System.out.println("length : " + vFiles.length);
        System.out.println("path : " + vFiles[0].getPath());
        System.out.println("canonicalpath : " + vFiles[0].getCanonicalPath());

        final DefaultMutableTreeNode activityNode = new DefaultMutableTreeNode("Activity");
        final DefaultMutableTreeNode fragmentNode = new DefaultMutableTreeNode("Fragment");

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Project");

        root.add(activityNode);
        root.add(fragmentNode);

        TreeModel treeModel = new DefaultTreeModel(root);

        tree.setModel(treeModel);

        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
        renderer.setLeafIcon(new ImageIcon("activity.png"));
        //UIManager.put("Tree.leafIcon", getClass().getResource("activity.png"));
       // tree.setCellRenderer(new ExplorerTreeRenderer());

        scanProject(vFiles[0].getCanonicalPath());

        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Label1.setText("Button Clicked");
                scanProject(vFiles[0].getCanonicalPath());

                activityMap = unitManager.getActivities();
                fragmentMap = unitManager.getFragments();

                Iterator iterator = activityMap.keySet().iterator();
                //Set Activity node
                while(iterator.hasNext()){
                    DefaultMutableTreeNode activitySubNode = new DefaultMutableTreeNode(activityMap.get(iterator.next()));
                    activityNode.add(activitySubNode);
                }

                //Set Fragment node
                iterator = fragmentMap.keySet().iterator();
                while (iterator.hasNext()){
                    DefaultMutableTreeNode fragmentSubNode = new DefaultMutableTreeNode(fragmentMap.get(iterator.next()));
                    fragmentNode.add(fragmentSubNode);
                }
            }
        });

        tree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                 //if enter key has been pressed
                if(e.getKeyCode()==10){
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

                    Object userObject = selectedNode.getUserObject();

                    if(userObject instanceof ActivityUnit || userObject instanceof FragmentUnit){
                        ComponentUnit componentUnit = (ComponentUnit)userObject;
                        openFileInEditor(project, componentUnit.getVirtualFile());
                    }
                }
            }
        });

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

                    Object userObject = selectedNode.getUserObject();

                    if(userObject instanceof ActivityUnit || userObject instanceof FragmentUnit){
                        ComponentUnit componentUnit = (ComponentUnit)userObject;
                        openFileInEditor(project, componentUnit.getVirtualFile());
                    }
                }
            }
        });
    }

    private void scanProject(String rootDir){
        System.out.println("scanning project");
        File file = new File(rootDir);

        DirExplorer dirExplorer = new DirExplorer(Filter.getInstance(), FileHandler.getInstance());

        dirExplorer.explore(file);

        UnitManager unitManager = UnitManager.getInstance();

        System.out.println(unitManager.getActivities().size());
        System.out.println(unitManager.getFragments().size());
    }

    public void openFileInEditor(Project project, VirtualFile virtualFile){
        FileEditorManager.getInstance(project).openFile(virtualFile, true);
    }
}