#!/bin/bash

## create comment
#curl --request "POST" "http://localhost:9001/comment/create" \
#	--header "content-type: application/json" \
#	--data "{\"postId\":\"somePostId\",\"body\":\"someBody\"}" \
#	--include \
#	&& echo

# get all comment for a single post
curl --request "GET" "http://localhost:9001/comment/post/somePostId" \
	--include \
	&& echo ""

