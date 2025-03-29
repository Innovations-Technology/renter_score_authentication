<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Renter Score Application</title>
</head>
<body>

    <table style="background: #F7F8FA; border: 0; border-radius: 0; width: 100%;" cellspacing="0" cellpadding="0">
     <tbody>
		<tr>
		<td>
		 <#include "header.ftlh" />
		</td>
	 </tr>
     <tr>
     <td class="tw-body" style="padding: 15px 15px;" align="center">
        <table style="background: #F7F8FA; border: 0; border-radius: 0;" cellspacing="0" cellpadding="0">
            <tbody>
            <tr>
            <td class="" style="width: 700px;" align="center">
			<p style="padding: 5px 5px 5px 5px; font-size: 13px; margin: 0 0 0px; color: #316fea;" align="right">
			</p>
			<table style="background: #ffffff; border: 0px; border-radius: 4px; width: 99.6672%; overflow: hidden;"
				   cellspacing="0" cellpadding="0">
				<tbody>
				<tr>
				<td class="" style="padding: 0px; width: 100%;" align="center">
                                
				<table dir="ltr" style="border: 0; width: 100%;" cellspacing="0" cellpadding="0">
					<tbody>
					<tr>
					  <td class="tw-card-body"
							style="padding: 20px 35px 20px 35px; text-align: left; color: #6f6f6f; font-family: sans-serif; border-top: 0;">
						<h1 class="tw-h1"
							style="font-size: 24px; font-weight: bold; mso-line-height-rule: exactly; line-height: 32px; margin: 0 0 20px; color: #474747;">
							Dear ${userName},</h1>
						<p class="" style="margin: 20px 0; font-size: 16px; mso-line-height-rule: exactly; line-height: 24px;">
						<span style="font-weight: 400;">Thank you for joining <strong>Renter Score Property application</strong>, an efficient and smart solution to your property engagement.</span><br/><br/><span
							style="font-weight: 400;">To complete your registration, please verify your email address on your mobile device using the token below. The verification token will expire after 10 minutes.</span>
							<table style="border: 0; width: 100%;" cellspacing="0" cellpadding="0">
							  <tbody>
							  <tr>
								<td>
									<table class="button mobile-w-full"
										   style="border: 0px; border-radius: 7px; margin: 0px auto; width: 525px; background-color: #198754; height: 50px;"
										   cellspacing="0" cellpadding="0" align="center">
										<tbody>
										<tr>
										 <td class="button__td " style="border-radius: 7px; text-align: center; width: 523px;"><!-- [if mso]>
											<a href="" class="button__a" target="_blank"
											   style="border-radius: 4px; color: #FFFFFF; display: block; font-family: sans-serif; font-size: 18px; font-weight: bold; mso-height-rule: exactly; line-height: 1.1; padding: 14px 18px; text-decoration: none; text-transform: none; border: 1px solid #316FEA;"> </a>
											<![endif]--> <!-- [if !mso]><!--> 
											<a
											class="button__a"
											style="border-radius: 4px; color: #ffffff; display: block; font-family: sans-serif; font-size: 18px; font-weight: bold; mso-height-rule: exactly; line-height: 1.1; padding: 14px 18px; text-decoration: none; text-transform: none; border: 0;"
											href=""
											target="_blank"
											rel="noopener">${verificationToken}</a> <!--![endif]-->
										 </td>
										</tr>
										</tbody>
									</table>
								</td>
							  </tr>
							  </tbody>
							</table>
                                            
						<p class="" style="margin: 20px 0; font-size: 16px; mso-line-height-rule: exactly; line-height: 24px;">
							Contact our support team if you have any questions or concerns.&nbsp;<a
								style="color: #316fea; text-decoration: none;"
								href="web-page-link" target="_blank" rel="noopener">Ask us any
							question</a></p>
						<p class="tw-signoff"
						   style="margin: 45px 0 5px; font-size: 16px; mso-line-height-rule: exactly; line-height: 24px;">
							Our best, <br/> Renter Score IT Team</p>
					 </td>
					</tr>
				  </tbody>
				</table>
				</td>
			  </tr>
			 </tbody>
		    </table>
		  </td>
		  </tr>
		 </tbody>
		</table>
		</td>
	  </tr>
	  <tr>
		<td>
		<p>
		 <#include "footer.ftlh" />
		</p>
		</td>
		</tr>
	 </tbody>
	</table>
</body>
</html>