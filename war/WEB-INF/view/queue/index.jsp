<%@ page contentType="text/html; charset=utf8" %>
<html>
<head>
	<title>web2feed</title>
</head>
<body>
	<form method="post" action="/queue/regist">
		<input type="text" name="url" value="" size="64" />
		<input type="submit" value="regist" />
	</form>
	
	<table>
		<tr>
			<th>id</th>
			<th>url</th>
			<th>createdAt</th>
		</tr>
		<% if (request.getAttribute("queues") != null) { %>
			<% java.util.List<orz.yanagin.web2feed.queue.Queue> queues = (java.util.List<orz.yanagin.web2feed.queue.Queue>)request.getAttribute("queues"); %>
			<% for (orz.yanagin.web2feed.queue.Queue queue : queues) { %>
				<table>
					<tr>
						<td><%= queue.getUrl() %></td>
						<td><%= queue.getUpdatedAt() %></td>
					</tr>
				</table>
			<% } %>
		<% } %>
	</table>
</body>
</html>
