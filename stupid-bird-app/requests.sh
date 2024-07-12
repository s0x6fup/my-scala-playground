#!/bin/bash

## create comment
#echo '[+] creating comments'
#curl --request 'POST' 'http://localhost:9001/comment/create' \
#	--header 'content-type: application/json' \
#	--data '{"postId":"somePostId","body":"someBody"}' \
#	--include \
#	&& echo && echo

# get all comment for a single post
echo '[+] getting comments by post id'
curl --request 'GET' 'http://localhost:9001/comment/post/somePostId' \
	--include \
	&& echo && echo

# get my comments (using anonymous for testing)
echo '[+] getting MY comments'
curl --request 'GET' 'http://localhost:9001/comment/post/my' \
	--include \
	&& echo && echo 

