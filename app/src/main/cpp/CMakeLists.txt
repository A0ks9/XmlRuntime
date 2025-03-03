cmake_minimum_required(VERSION 3.22.1)

include(FetchContent)

project(xmlParser LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

add_library(xmlParser
        SHARED
        ${CMAKE_CURRENT_SOURCE_DIR}/xmlParser.cpp
)

FetchContent_Declare(
        expat
        GIT_REPOSITORY https://github.com/libexpat/libexpat.git
        GIT_TAG R_2_6_4
        SOURCE_SUBDIR expat/
)

# Download and configure Expat.
FetchContent_MakeAvailable(expat)

set(RAPIDJSON_BUILD_EXAMPLES OFF)

FetchContent_Declare(
        rapidjson
        GIT_REPOSITORY https://github.com/Tencent/rapidjson.git
        GIT_TAG master
)
FetchContent_MakeAvailable(rapidjson)

target_include_directories(xmlParser PRIVATE ${rapidjson_SOURCE_DIR}/include)

find_library(log-lib log)

target_link_libraries(xmlParser
        PUBLIC
        expat
        android
        ${log-lib}
)