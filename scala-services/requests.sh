#!/bin/bash

#echo '[+] creating comments'
#curl --request 'POST' 'http://localhost:9001/comment/create' \
#	--header 'content-type: application/json' \
#	--data '{"postId":"somePostId","body":"someBody"}' \
#	--include \
#	&& echo && echo

#echo '[+] getting comments by post id'
#curl --request 'GET' 'http://localhost:9001/comment/post/somePostId' \
#	--include \
#	&& echo && echo

#echo '[+] getting MY comments'
#curl --request 'GET' 'http://localhost:9001/comment/post/my' \
#	--include \
#	&& echo && echo

echo '[+] checking authorization'
curl --request 'POST' 'http://localhost:9002/authorization/is-authorized' \
	--header 'content-type: application/json' \
	--data '{"permission":"health.read"}' \
	--include \
	&& echo && echo
