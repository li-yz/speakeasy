package serialprocess;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AdjustRandIndex {
	double [][]R;
	public AdjustRandIndex(){
		
	};
	public AdjustRandIndex(int numruns){
		R=new double [numruns][numruns];
	}
	
	//������������֮��ARIֵ�ķ���
	public void calcuARI(Map<String, List<String>> C1,Map<String, List<String>> C2,int RindexI,int Rindexj,int vNum){
		int C1size=C1.size();
		int C2size=C2.size();
		
		//Ϊ�˷���ʹ��һ�� C1size * C1size �ľ���������ARIֵ���ֱ�����������C1��C2���������������֮���ӳ��
		Map<String, Integer> c1map=new HashMap<String, Integer>();
		Map<String, Integer> c2map=new HashMap<String, Integer>();
		Iterator c1iter=C1.entrySet().iterator();
		int c1seq=0;
		while(c1iter.hasNext()){
			Map.Entry entry=(Map.Entry)c1iter.next();
			String communityName=(String)entry.getKey();
			c1map.put(communityName, c1seq);
			c1seq++;
		}
		
		Iterator c2iter=C2.entrySet().iterator();
		int c2seq=0;
		while(c2iter.hasNext()){
			Map.Entry entry=(Map.Entry)c2iter.next();
			String communityName=(String)entry.getKey();
			c2map.put(communityName, c2seq);
			c2seq++;
		} 
		//�м�������,Ԫ��ֵ��ARI���㹫ʽ�е�nij��ֵ��ע�⣺quantityM���ǶԳƵ�
		int[][] quantityM=new int[c1seq][c2seq];//
		
		c1iter=C1.entrySet().iterator();
		
		while(c1iter.hasNext()){
			Map.Entry entry=(Map.Entry)c1iter.next();
			String cnameInC1=(String)entry.getKey();
			List<String>c1NodesList= C1.get(cnameInC1);
			int x=c1map.get(cnameInC1);//x������C1�е�������cnameInC1��Ӧ�����
			c2iter=C2.entrySet().iterator();
			while(c2iter.hasNext()){
				Map.Entry entry2=(Map.Entry)c2iter.next();
				String cnameInC2=(String)entry2.getKey();
				List<String>c2NodesList= C2.get(cnameInC2);
				int y=c2map.get(cnameInC2);//y������C2�е�������cnameInC2��Ӧ�����
				for(int n1=0;n1 <c1NodesList.size();n1++ ){
					for(int n2=0;n2 <c2NodesList.size();n2++ ){
						if(c1NodesList.get(n1).equals(c2NodesList.get(n2)))
							quantityM[x][y]++;
					}
				}
			}
		}
		
		double sigmaij=0.0d;
		double sigmaRow=0.0d;
		double sigmaColomn=0.0d;
		for(int i=0;i < C1size;i++){
			int rowSum=0;
			for(int j=0;j < C2size;j++){
				if(quantityM[i][j] >= 2){
					sigmaij+=quantityM[i][j]*(quantityM[i][j]-1)/2;
					rowSum+=quantityM[i][j];
					sigmaRow+=rowSum*(rowSum-1)/2;
				}
			}
		}
		
		for(int ii=0;ii < C2size;ii++){//�к�
			int columnSum=0;
			for(int jj=0;jj < C1size;jj++){//�к�
				columnSum+=quantityM[jj][ii];
				sigmaColomn+=columnSum*(columnSum-1)/2;
			}
		}
		
		double fenzi=sigmaij-sigmaRow*sigmaColomn/(vNum*(vNum-1)/2);
		double fenmu=(0.5*(sigmaRow+sigmaColomn))-(2*(sigmaRow*sigmaColomn)/(vNum*(vNum-1)));
		double ari=fenzi/fenmu;
		this.setR(RindexI, Rindexj, ari);
	}
	public synchronized void setR(int i,int j,double value) {
		this.R[i][j]=value;
	}
}
