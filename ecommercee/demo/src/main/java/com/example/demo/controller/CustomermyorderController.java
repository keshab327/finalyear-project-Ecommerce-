package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dao.OrderRepository;
import com.example.demo.dao.ProductreportRepository;
import com.example.demo.dao.ShopRegisterRepository;
import com.example.demo.dao.UserdetailRepository;
import com.example.demo.enittiy.Order;
import com.example.demo.enittiy.Productreport;
import com.example.demo.enittiy.ShopRegister;
import com.example.demo.enittiy.Userdetail;



@Controller
public class CustomermyorderController {
	
	
	@Autowired
	OrderRepository orderrepo;
	@Autowired
	ShopRegisterRepository shoprepo;
	
	@Autowired
	ProductreportRepository productreportrepo;
	@Autowired
	UserdetailRepository userdetailrepo;
	
	
	
	@GetMapping("/customermyorder/myorders")
	public String show(Model model ,HttpServletRequest request)//for session to work use in function parameter of mapping//
	{
		int customer_id;
		try {
		System.out.println("\n\n in my orders");
	customer_id=  (int) request.getSession().getAttribute("user_id");
		}catch(Exception e) {
			
			return "redirect:/login";
		}

		
	

	
Userdetail user=userdetailrepo.findById(customer_id).get();
List<Order> orders1=user.getOrder();
List<Order> orders2=new ArrayList<Order>();
for(Order o:orders1) {
	
	
	if(o.getCancellcheck()==false&&o.getStatus().equalsIgnoreCase("CONFIRMED")&&o.getDeliverstatus().equalsIgnoreCase("NOTDELIVERED")) {
		
		orders2.add(o);
	}
}
	
		model.addAttribute("orders", orders2);
	
		return "myorders_deprt";
		
	}
	
	
	
	
	
	
	@GetMapping("/customermyorder/delivered")
	public String deliver(Model model ,HttpServletRequest request)//for session to work use in function parameter of mapping//
	{
		
		System.out.println("\n\n in my orders");
	int customer_id=  (int) request.getSession().getAttribute("user_id");
	
	
Userdetail user=userdetailrepo.findById(customer_id).get();
List<Order> orders1=user.getOrder();
List<Order> orders2=new ArrayList<Order>();
for(Order o:orders1) {
	
	
	if(o.getDeliverstatus().equalsIgnoreCase("DELIVERED")) {
		
		orders2.add(o);
	}
}
	
		model.addAttribute("orders", orders2);
	
		return "myorders_delivered";
		
	}
	
	
	
	
	@GetMapping("/customermyorder/report/{orderid}")
	String showProductDetails(@PathVariable("orderid") int orderid, Optional<Order> order, Model model,HttpServletRequest request) {
	
	//	System.out.println("shopid:"+shopid);
		try {
			
			if (orderid != 0) {
				 order = orderrepo.findById(orderid);
				 model.addAttribute("order",order);
				 
				
			
			
			
				if (order.isPresent()) {
					model.addAttribute("productid", order.get().getProductid());
					model.addAttribute("productname", order.get().getProductname());

					

					model.addAttribute("provience",order.get().getProvience());
					model.addAttribute("district",order.get().getDistrict());
					model.addAttribute("wardno",order.get().getWardno());
					
					model.addAttribute("shippingdistrict",order.get().getShippingdistrict());
					model.addAttribute("shippingprovience",order.get().getShippingprovience());
					model.addAttribute("shippingwardno",order.get().getShippingwardno());
					
					
					
					model.addAttribute("totalamountperitem", order.get().getTotalamountperitem());
					model.addAttribute("phonenumber", order.get().getPhonenumber());
					model.addAttribute("email", order.get().getEmail());
					model.addAttribute("email", order.get().getEmail());
					model.addAttribute("deliverstatus", order.get().getDeliverstatus());
					model.addAttribute("departstatus", order.get().getDepartstatus());
					model.addAttribute("orderedDate", order.get().getOrderedDate());
					model.addAttribute("id", order.get().getId());
					model.addAttribute("shopid",order.get().getShopid());
					

				}
	
			}
			}catch(Exception e) {
				System.out.print("exception occured");
			}
		return "customer_report";
	
	}
	

	 @PostMapping("/Customermyorder/submitreport") 
	  public @ResponseBody ResponseEntity<?>  saveProduct(@ModelAttribute("product") Productreport report ,@RequestParam("orderid") int orderid ) {
		  
		//  System.out.println("\n report details "+report.getOrderid()+"\norder id"+report.getReport()+"\n"+report.getReportstatus());
		  try {
			  Optional<Productreport> report1=productreportrepo.findByOrderr_Id(orderid);
			  if(report1.isEmpty()) {
				  report.setOrderr(orderrepo.findById(orderid).get());
				  
				  
			  }
			  else {
				  
				  report.setId(report1.get().getId());
				  report.setOrderr(report1.get().getOrderr());
				  
			  }
			  productreportrepo.save(report);
		 return new ResponseEntity<>("your report has been submmitted - ",HttpStatus.OK);
		  
	  }catch (Exception e) {
		  
		  e.printStackTrace();
			
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		  
	  }
	
	
	 }



@GetMapping("/cancell/order")
public String cancell(@RequestParam("orderid") int orderid,Model model,HttpServletRequest request) {
	
	 
	System.out.print("\n\n\n orderid"+orderid);
	
	int customer_id;
	try {
	System.out.println("\n\n in my orders");
customer_id=  (int) request.getSession().getAttribute("user_id");
	}catch(Exception e) {
		
		return "redirect:/login";
	}

	



Userdetail user=userdetailrepo.findById(customer_id).get();
Order oo=orderrepo.findById(orderid).get();
oo.setCancellcheck(true);
orderrepo.save(oo);
List<Order> orders1=user.getOrder();
List<Order> orders2=new ArrayList<Order>();
int index=0;

/*
for(Order o:orders1) {

	
	if(o.getId()==orderid) {
		Date date=new Date();
		o.setCancelldate(date);
		o.setCancellcheck(true);
		o.setCancellstatus("CANCELLED");
		orders1.remove(index);
		
		orders1.add(o);
	user.setOrder(orders1);
	userdetailrepo.save(user);
	break;
		
}
	
	++index;
}	
	*/



//change for shop

//ShopRegister shop=shoprepo.findById(null)


	model.addAttribute("orders", orders2);

	//return "myorders_deprt";
	
	
	
	
	
	
	
	return "redirect:/customermyorder/myorders";
	
}
	 
	 
	 
	 
	 
@GetMapping("/cancelled/order")
public String cancelll(Model model,HttpServletRequest request) {
	
	
	
	int customer_id;
	try {
	System.out.println("\n\n in my orders");
customer_id=  (int) request.getSession().getAttribute("user_id");
	}catch(Exception e) {
		
		return "redirect:/login";
	}
	
	

Userdetail user=userdetailrepo.findById(customer_id).get();
List<Order> orders1=user.getOrder();
List<Order> orders2=new ArrayList<Order>();
int index=0;
for(Order o:orders1) {
	
	
	if(o.getCancellcheck()==true) {
		orders2.add(o);
	}
	
	
}
	
	
	model.addAttribute("orders", orders2);
	
	return "cancell_orders";
	
	
}
	 






}
	
	
	
	 
	
	

