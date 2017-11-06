<#-- @ftlvariable name="" type="by.dev.madhead.jarvis.model.Email" -->
<#switch build.status.name()>
	<#case "PASSED">
		<#assign buildDescription = "Build ${build.number} passed.">
		<#assign background = "#baecb7">
		<#assign color = "#32a32d">
		<#break>
	<#case "FIXED">
		<#assign buildDescription = "Build ${build.number} was fixed.">
		<#assign background = "#baecb7">
		<#assign color = "#32a32d">
		<#break>
	<#case "BROKEN">
		<#assign buildDescription = "Build ${build.number} was broken.">
		<#assign background = "#fdcdce">
		<#assign color = "#df192a">
		<#break>
	<#case "STILL_BROKEN">
		<#assign buildDescription = "Build ${build.number} is still broken.">
		<#assign background = "#fdcdce">
		<#assign color = "#df192a">
		<#break>
	<#case "FAILED">
		<#assign buildDescription = "Build ${build.number} failed.">
		<#assign background = "#fdcdce">
		<#assign color = "#df192a">
		<#break>
	<#case "STILL_FAILING">
		<#assign buildDescription = "Build ${build.number} is still failing.">
		<#assign background = "#fdcdce">
		<#assign color = "#df192a">
		<#break>
	<#case "UNKNOWN">
		<#assign buildDescription = "Build ${build.number} collapsed.">
		<#assign background = "#ccc">
		<#assign color = "#707070">
		<#break>
</#switch>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
		<style type="text/css">
			body {
				margin: 0;
				padding: 0;
			}

			table {
				/*Debug borders*/
				border: 1px black solid;
			}

			table.content {
				width: 570px;

				margin-bottom: 15px;

				border-collapse: collapse;
			}
		</style>
	</head>
	<body>
		<table cellpadding="0" cellspacing="0" width="100%">
			<tr>
				<td>
					<table class="content" align="center" cellpadding="0" cellspacing="0">
						<tr style="color: #606060; font-size: 20px">
							<td>
								<strong>
								<#if repo.link??>
									<a href="${repo.link}" style="color: #606060;">${repo.slug}</a>
								<#else>
									<span>
									${repo.slug}
									</span>
								</#if>
								</strong>
							<#if build.branch??>
								(${build.branch})
							</#if>
							</td>
						</tr>
					</table>

					<table class="content" align="center" cellpadding="0" cellspacing="0">
						<tr style="background-color: ${background}; color: ${color}">
							<td>
								<img width="40" height="40" src="cid:status.png">
							${buildDescription}
							</td>
							<td>
								<img width="40" height="40" src="cid:stopwatch.png">
							${build.durationForHumans}
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</body>
</html>
