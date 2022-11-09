#include<iostream>

using namespace std;

constexpr int a(){
    int ret = 0;
    for(int i=1;i<=1000;i++){
        ret+=i;
    }
    return ret;
}
int main(){
    cout << a();
}