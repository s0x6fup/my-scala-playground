# create comment
curl --request "POST" "http://localhost:9001/comment/create" \
	--header "content-type: application/json" \
	--data "{\"postId\":\"somePostId\",\"body\":\"someBody\"}"

## create comment
#curl --request "POST" "http://localhost:9001/comment/create" \
#	--header "content-type: application/json" \
#	--data "{\"postId\":\"somePostId\",\"body\":\"someBody\"}"




