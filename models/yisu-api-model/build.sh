#!/bin/bash
# 构建完整的OpenAPI文档
swagger-cli bundle .\yisu-api-v1.json --outfile _build/yisu-api.json --type json