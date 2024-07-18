package com.spring.app.chatting.websockethandler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

// === #233. 웹채팅관련15 ===
@Service
public class ChattingMongoOperations {

	@Autowired
 // private MongoTemplate mongo;    // MongoTemplate 는 MongoOperations 의 구현 클래스 이다.
	private MongoOperations mongo;  // MongoOperations 는 Interface 이다. 
		                            // MongoTemplate 이나 MongoOperations 둘 중에 어느 것을 사용해도 사용할 수 있지만 Interface 로 주입 받아서 사용하는 것이 조금 더 좋은 방법이다.
	
	public void insertMessage(Mongo_messageVO dto) throws Exception {
	//	System.out.println("~~~~~~~ 확인용 insertMessage 호출함.");
		
		try {
    /*  System.out.println("_id : " + dto.get_id() +  "\n"
        		         + "message : " + dto.getMessage() + "\n"
        		         + "to : " + dto.getTo() + "\n"
        		         + "type : " + dto.getType() + "\n"
        		         + "userid : " + dto.getUserid() + "\n"
        		         + "currentTime : " + dto.getCurrentTime() ); 
    */ 
        
		  // >>> 데이터 추가 <<< 
		     mongo.save(dto, "chatting"); // 없으면 추가, 있으면 수정.  이것을 실행되면 mongodb 의 데이터베이스 mydb 에 chatting 라는 컬렉션이 없다라도 자동적으로 먼저  chatting 컬렉션을 생성해준 다음에 도큐먼트를 추가시켜준다. 
		  // mongo.save(dto);             // 없으면 추가, 있으면 수정.  com.spring.app.chatting.websockethandler.Mongo_messageVO 클래스 생성시 @Document(collection = "chatting") 어노테이션을 설정했으므로 두번째 파라미터로 "chatting" 은 생략가능하다.  
			
		}catch(Exception e) {
			e.printStackTrace();
			
			throw e;
		}
	}
	
	
	public List<Mongo_messageVO> listChatting() {
		List<Mongo_messageVO> list = null;
		
		try {
		 	
		 // >>> 정렬 결과 조회 <<<
			Query query = new Query();
			query.with(Sort.by(Sort.Direction.ASC, "_id"));
			list = mongo.find(query, Mongo_messageVO.class);
			
		 // >>> [참고] 전체조회 <<< 
		 // list = mongo.findAll(Mongo_messageVO.class);
		
		}catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
}
