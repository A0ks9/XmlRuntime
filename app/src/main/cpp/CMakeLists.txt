cmake_minimum_required(VERSION 3.22.1)

include(FetchContent)

project(xmlParser LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

FetchContent_Declare(
        expat
        GIT_REPOSITORY https://github.com/libexpat/libexpat.git
        GIT_TAG R_2_6_4
        SOURCE_SUBDIR expat/
)

# Download and configure Expat.
FetchContent_MakeAvailable(expat)

FetchContent_Declare(
        rapidjson
        GIT_REPOSITORY https://github.com/Tencent/rapidjson.git
        GIT_TAG v1.1.0
        SOURCE_SUBDIR include/
)

FetchContent_MakeAvailable(rapidjson)



find_library(log-lib log)

target_link_libraries(xmlParser
        PUBLIC
        expat
        rapidjson
        android
        ${log-lib}
)