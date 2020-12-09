package main

import (
	"fmt"
	"log"
	"net/http"
)

func main() {
	fs := http.FileServer(http.Dir("public"))
	fmt.Println("start server http://localhost:9000")
	log.Fatal(http.ListenAndServe(":9000", fs))
}
