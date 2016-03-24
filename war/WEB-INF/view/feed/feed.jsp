<%@ page contentType="text/xml; charset=utf8" %><?xml version="1.0"?>
<rss version="2.0">
	<channel>
		<title>web2feed</title>
		<link>http://web2feed-1200.appspot.com/</link>
		<description>web2feed</description>
		<% if (request.getAttribute("resources") != null) { %>
			<% java.util.List<orz.yanagin.web2feed.resource.Resource> resources = (java.util.List<orz.yanagin.web2feed.resource.Resource>)request.getAttribute("resources"); %>
			<% for (orz.yanagin.web2feed.resource.Resource resource : resources) { %>
				<item>
					<title><%= resource.getTitle() %></title>
					<link><%= resource.getUrl() %></link>
					<description><![CDATA[<%= resource.getDescription() %>]]></description>
					<pubDate><%= resource.getDate() %></pubDate>
  					<guid>http://web2feed-1200.appspot.com/resource/<%= resource.getKeyString() %></guid>
				</item>
			<% } %>
		<% } %>

	</channel>
</rss>