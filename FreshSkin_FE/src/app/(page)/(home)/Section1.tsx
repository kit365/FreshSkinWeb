import CategoryList from "@/app/components/Category/CategoryList";
import Title from "@/app/components/title/Title";

export default function Section1(props: any) {

    const { dataInit = [] } = props;

    return (
        <>
            <div className="container mx-auto mb-[40px]">
                <Title title="danh mục hot" link="/products"/>
                <CategoryList data={dataInit}/>
            </div>
        </>
    )
}