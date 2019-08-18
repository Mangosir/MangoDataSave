cursor用行和列（column）构成，是每行的集合。使用前必须知道每列的名称和数据类型（见下常用应用场景）。

关于 Cursor 的重要方法：

· close() 关闭游标，释放资源

· copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) 在缓冲区中检索请求的列的文本，并将其存储。

· getColumnCount() 返回所有列的总数

· getColumnIndex(String columnName) 返回指定列的名称，如果不存在返回-1

· getColumnIndexOrThrow(String columnName) 从零开始返回指定列名称，如果不存在将抛出IllegalArgumentException 异常。

· getColumnName(int columnIndex) 从给定的索引返回列名

· getColumnNames() 返回一个字符串数组的列名

· getCount() 返回Cursor 中的行数

· moveToFirst() 移动光标到第一行

· moveToLast() 移动光标到最后一行

· moveToNext() 移动光标到下一行

· moveToPosition(int position) 移动光标到一个绝对的位置

· moveToPrevious() 移动光标到上一行