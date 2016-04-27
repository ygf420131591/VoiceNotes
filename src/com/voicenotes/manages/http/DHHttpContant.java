package com.voicenotes.manages.http;

public class DHHttpContant {
	//http ��ʼ��
	public static final String HTTP_VERSION_DEFAULT   =  "http/1.1";
	
	//http ͷ�ؼ���
	public static final String HTTP_HEAD_CONTENT_LENGTH_KEY = "Content-Length";
	public static final String HTTP_HEAD_CONTENT_TYPE_KEY = "Content-Type";
	public static final String HTTP_HEAD_COOKID_KEY = "cookID";
	public static final String HTTP_HEAD_SEQUENCE_KEY = "Sequence";
	
	//http ͷ��ֵ
	public static final String HTTP_HEAD_CONTENT_TYPE_VALUE = "application/octet-stream";
	
	//http ��Ϣ����
	public static final int HTTP_MSG_TYPE_REQUEST   =  1; //֪ͨ/������Ϣ
	public static final int HTTP_MSG_TYPE_RESPONSE   =  2; //��Ӧ��Ϣ
	
	//http �������
	public static final int HTTP_PARSER_DATA_SUCCESS = 0; //�����ɹ�
	public static final int HTTP_PARSER_DATA_INCOMPLETE = 1; //���ݲ���������Ҫ��һ�����ݰ�����������
	public static final int HTTP_PARSER_DATA_ERROR = 2; //http��װ���������﷨

}
