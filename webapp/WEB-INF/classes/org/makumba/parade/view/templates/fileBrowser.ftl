<HTML><HEAD><TITLE>${rowName} files</TITLE>
<link rel='StyleSheet' href='/style/parade.css' type='text/css'>
<link rel='StyleSheet' href='/style/files.css' type='text/css'>
</HEAD><BODY class='files'>

<#if opResult != "">
	<#if success>
	<div class='success'>${opResult}</div>
	<#else>
	<div class='failure'>${opResult}</div>
	</#if>
</#if>

<h2>[<a href='/servlet/browse?display=file&context=${rowName}'>${rowName}</a>]/<#list parentDirs as parentDir><a href='/servlet/browse?display=file&context=${rowName}&path=${parentDir.path}'>${parentDir.directoryName}</a>/</#list><img src='/images/folder-open.gif'></h2>
<div class='pathOnDisk'>${pathOnDisk}</div>
<table class='files'>
<tr><th></th><th colspan='2'>
<a href='/File.do?display=command&view=newDir&context=${rowName}&path=${path}' target='command' title='Create a new directory'><img src='/images/newfolder.gif' align='right'></a>
<a href='/uploadFile.jsp?context=${rowName}&path=${path}' target='command' title='Upload a file'><img src='/images/uploadfile.gif' align='right'></a>
<a href='/File.do?display=command&view=newFile&context=${rowName}&path=${path}' target='command' title='Create a new file'><img src='/images/newfile.gif' align='right'></a> Name</th>
<th>Age</th><th>Size</th>

<script language="JavaScript">
<!--
function deleteFile(path, name) {
if(confirm('Are you sure you want to delete the file '+name+' ?'))
{
	url='/File.do?display=file&context=${rowName}&path=${pathEncoded}&op=deleteFile&params='+encodeURIComponent(path);
	location.href=url;
}
}
-->
</script>
<th>CVS <a href='/Cvs.do?op=check&context=${rowName}&params=${path}' target='command'><img src='/images/cvs-query.gif' alt='CVS check status' border='0'></a>
<a href='/Cvs.do?op=update&context=${rowName}&params=${path}' target='command'><img src='/images/cvs-update.gif' alt='CVS local update' border='0'></a>
<a href='/Cvs.do?op=rupdate&context=${rowName}&params=${path}' target='command'><img src='/images/cvs-update.gif' alt='CVS recursive update' border='0'></a>
</th>
</tr>

<#list fileViews as file>
<tr class="<#if (file_index % 2) = 0>odd<#else>even</#if>">

<!-- FILE -->
<#if file.isDir>
<td><img src='/images/folder.gif'></td>
<td colspan='2'><a href='/File.do?browse&display=file&context=${rowName}&path=${file.path}'>${file.name}</a></td>
<#else>
<td><img src='/images/${file.image}.gif'></td>
<td>
<#if file.isLinked>
<a href='${file.address}'>${file.name}</a>
<#else>${file.name}</#if>
</td>
<td align='right'>
<a href='/File.do?op=editFile&context=${rowName}&path=${path}&file=${file.name}'><img src='/images/edit.gif' alt='Edit ${file.name}'></a>
<a href="javascript:deleteFile('${file.pathEncoded}','${file.name}')"><img src='/images/delete.gif' alt='Delete ${file.name}'></a>
</td>
</#if>
<td><a title='${file.dateLong}'>${file.dateNice}</a></td>
<#if !file.isDir>
<#if !file.isEmpty><td><a title='${file.sizeLong} bytes'>${file.sizeNice}</a><#else><td><i>empty<i></td></#if>
<#else>
</td><td>
</#if>
</td>

<!-- CVS -->
<td>
<#if file.cvsIsNull>
<#if file.isConflictBackup><a title='Backup of your working file, can be deleted once you resolved its conflicts with CVS'>Conflict Backup</a>
<#else>
<a target='command' href='/Cvs.do?context=${rowName}&path=${path}&file=${file.path}&op=add'><img src='/images/cvs-add.gif' alt='add'></a>
<a target='command' href='/Cvs.do?context=${rowName}&path=${path}&file=${file.path}&op=addbin'><img src='/images/cvs-add-binary.gif' alt='add binary'></a>
</#if>
<#else>
<#switch file.cvsStatus>

<#case 101>
<!-- IGNORED -->
<div class='cvs-ignored'>ignored</div>
<#break>

<!-- UNKNOWN -->
<#case -1>
???
<#break>
<!-- UP_TO_DATE -->

<#case -1>
<#if file.isDir>
<a href='${file.cvsWebLink}' title='CVS log'>(dir)</a>
<#else>
<a href='${file.cvsWebLink}' title='CVS log'>${file.cvsRevision}</a>
</#if>
<#break>

<!-- LOCALLY_MODIFIED -->
<#case 1>
<#if file.isDir>
<a href='${file.cvsWebLink}' title='CVS log'>(dir)</a>
<#else>
<a href='${file.cvsWebLink}' title='CVS log'>${file.cvsRevision}</a>
<a target='command' href='/servlet/browse?context=${rowName}&path=${path}&file=${file.path}&display=command&view=commit'><img src='/images/cvs-committ.gif' alt='CVS commit'></a>
<a target='command' href='/Cvs.do?context=${rowName}&path=${path}&file=${file.path}&op=diff'><img src='/images/cvs-diff.gif' alt='CVS diff'></a>
</#if>
<#break>

<!-- NEEDS_CHECKOUT -->
<#case 2>
<#if file.isDir>
<a href='${file.cvsWebLink}' title='CVS log'>(dir)</a>
<#else>
<a href='${file.cvsWebLink}' title='CVS log'>${file.cvsRevision}</a>
<a target='command' href='/Cvs.do?context=${rowName}&path=${path}&file=${file.path}&op=updatefile'><img src='/images/cvs-update.gif' alt='CVS checkout'></a>
<a target='command' href='/Cvs.do?context=${rowName}&path=${path}&file=${file.path}&op=deletefile'><img src='/images/cvs-remove.gif' alt='CVS remove'></a>
</#if>
<#break>

<!-- NEEDS_UPDATE -->
<#case 3>
<#if file.isDir>
<a href='${file.cvsWebLink}' title='CVS log'>(dir)</a>
<#else>
<a href='${file.cvsWebLink}' title='CVS log'>${file.cvsRevision}</a>
<a target='command' href='/Cvs.do?context=${rowName}&path=${path}&file=${file.path}&op=updatefile'><img src='/images/cvs-update.gif' alt='CVS update'></a>
</#if>
<#break>

<!-- ADDED -->
<#case 4>
<a href='${file.cvsWebLink}' title='CVS log'>${file.cvsRevision}</a>
<a target='command' href='/servlet/browse?context=${rowName}&path=${path}&file=${file.path}&display=command&view=commit'><img src='/images/cvs-committ.gif' alt='CVS commit'></a>
<#break>

<!-- DELETED -->
<#case 5>
<a href='${file.cvsWebLink}' title='CVS log'>${file.cvsRevision}</a>
<a target='command' href='/servlet/browse?context=${rowName}&path=${path}&file=${file.path}&display=command&view=commit'><img src='/images/cvs-committ.gif' alt='CVS commit'></a>
<#break>

<!-- CONFLICT -->
<#case 6>
<a href='${file.cvsWebLink}' title='CVS log'><b><font color='red'>Conflict</font></b>${file.cvsWebLink}</a>
<a target='command' href='/servlet/browse?context=${rowName}&path=${path}&file=${file.path}&display=command&view=commit'><img src='/images/cvs-committ.gif' alt='CVS commit'></a>
<a target='command' href='/Cvs.do?context=${rowName}&path=${path}&file=${file.path}&op=diff'><img src='/images/cvs-diff.gif' alt='CVS diff'></a>
<#break>

</#switch>
</#if>

</td>
</tr>
        
</#list>

</TABLE></BODY></HTML>

