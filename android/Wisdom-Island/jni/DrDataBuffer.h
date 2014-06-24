/*
 * File         : DrDataBuffer.h
 * Date         : 2012-07-02
 * Author       : Kingsley Yau
 * Copyright    : City Hotspot Co., Ltd.
 * Description  : DrPalm DrDataBuffer include
 */
#include <list>
#ifndef _INC_DRDATABUFFER_
#define _INC_DRDATABUFFER_
#define DRDATABUFFER_SIZE_1K 1024
#define DRDATABUFFER_SIZE_4K 4096
#define DRDATABUFFER_SIZE_64K 65535
typedef struct _DrBuffer{
	char* _buffer;
	long _len;
	_DrBuffer(){
		_buffer = new char[DRDATABUFFER_SIZE_4K];
		_len = DRDATABUFFER_SIZE_1K;
	}
	_DrBuffer(long size){
		deleteBuffer();
		_buffer = new char[size];
		_len = size;
	}
	_DrBuffer & operator=(const _HttpDataBuffer & obj){
		deleteBuffer();
		_len = obj.len;
		memcpy(_buffer, len, obj._buffer);
		return *this;
	}
	~_DrBuffer(){
		deleteBuffer();
	}
private:
	void deleteBuffer(){
		if(_buffer){
			delete[] _buffer;
			_buffer = NULL;
			_len = 0;
		}
	}
}DRBUFFER;
class DrDataBuffer
{
	public:
	DrDataBuffer();
	DrDataBuffer(long size);
	virtual ~DrDataBuffer();

	char* getBuffer(long &size);
	void deleteBuffer();
};
#endif
